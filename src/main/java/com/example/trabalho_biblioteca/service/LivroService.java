package com.example.trabalho_biblioteca.service;

// Adicione estas importações:
import com.example.trabalho_biblioteca.model.*;
import com.example.trabalho_biblioteca.repository.CategoriaRepository;
import com.example.trabalho_biblioteca.repository.LivroRepository; // Para LivroRepository
import jakarta.annotation.PostConstruct; // Para @PostConstruct
import org.springframework.beans.factory.annotation.Autowired; // Para @Autowired
import org.springframework.beans.factory.annotation.Value; // Para @Value
import org.springframework.core.io.Resource; // Para Resource
import org.springframework.core.io.UrlResource; // Para UrlResource
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.ResponseEntity; // Para ResponseEntity
import org.springframework.stereotype.Service; // Para @Service
import org.springframework.web.bind.annotation.PathVariable; // Para @PathVariable (se usado no service, senão remova)
import org.springframework.web.bind.annotation.RequestParam; // Para @RequestParam (se usado no service, senão remova)
import org.springframework.web.multipart.MultipartFile; // Para MultipartFile

import java.io.File; // Para a classe File (usada na sua lógica de deletar)
import java.io.IOException; // Para IOException
import java.net.MalformedURLException; // Para MalformedURLException
import java.nio.file.Files; // Para a classe Files
import java.nio.file.Path; // Para a classe Path
import java.nio.file.Paths; // Para a classe Paths
import java.nio.file.StandardCopyOption; // Para StandardCopyOption
import java.util.List; // Para List
import java.util.UUID; // Para UUID
import java.time.LocalDate;
import java.time.Period;
import java.util.stream.Collectors;


@Service
public class LivroService {
    @Autowired
    LivroRepository livroRepository;

    @Autowired
    CategoriaRepository categoriaRepository;

    @Autowired
    AutorService autorService;

    @Value("${storage.pdf.path}")
    private String storagePath;

    @Value("${storage.capas.path}")
    private String capasPath; // Caminho para o diretório das capas

    private Path rootLocation; // Para PDFs
    private Path capasLocation; // Para Capas

    @PostConstruct
    public void init(){
        this.rootLocation = Paths.get(storagePath);
        this.capasLocation = Paths.get(capasPath); // Inicializa o caminho das capas
        try{
            Files.createDirectories(rootLocation);
            Files.createDirectories(capasLocation); // Cria o diretório das capas se não existir
        } catch (IOException e) {
            throw new RuntimeException("Não foi possível inicializar os diretórios de armazenamento", e);
        }
    }

    public Livro salvarLivro(MultipartFile file, MultipartFile capa, String autor, String titulo, String descricao, String nomeCategoria, ClassificacaoIndicativa classificacaoIndicativa) {
        // Verificando se o arquivo é um PDF
        String tipoArquivo = file.getContentType();
        if (!"application/pdf".equals(tipoArquivo)) {
            throw new IllegalArgumentException("O arquivo precisa ser um PDF.");
        }

        //verificando se o arquivo é uma imagem
        String tipoCapa = capa.getContentType();
        if(tipoCapa == null || (!tipoCapa.equals("image/jpeg") && !tipoCapa.equals("image/png"))){
            throw new IllegalArgumentException("A capa precisa ser um PNG ou JPEG.");
        }
        // Gerando nome único para o arquivo pdf
        String nomeArquivoPdf = UUID.randomUUID().toString() + "-" + file.getOriginalFilename();

        //Gerando um nome único para a capa do livro
        String nomeArquivoCapa = UUID.randomUUID().toString() + "-" + capa.getOriginalFilename();

        // Definindo o caminho completo onde o arquivo PDF será salvo
        Path destinoArquivoPdf = this.rootLocation.resolve(nomeArquivoPdf);

        // Definindo o caminho completo de onde a capa será salva
        Path destinoArquivoCapa = this.capasLocation.resolve(nomeArquivoCapa);

        try {
            // Verificando se o arquivo PDF não é nulo e transferindo para o diretório de armazenamento
            if (file != null && !file.isEmpty()) {
                Files.copy(file.getInputStream(), destinoArquivoPdf, StandardCopyOption.REPLACE_EXISTING);
            } else {
                throw new IllegalArgumentException("Arquivo PDF não foi enviado corretamente.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar o arquivo PDF: " + nomeArquivoPdf, e);
        }

        try {
            // Verificando se a capa não é nula e transferindo para o diretório de armazenamento
            if (capa != null && !capa.isEmpty()) {
                Files.copy(capa.getInputStream(), destinoArquivoCapa, StandardCopyOption.REPLACE_EXISTING);
            } else {
                throw new IllegalArgumentException("Capa não foi enviada corretamente.");
            }
        } catch (IOException e) {
            throw new RuntimeException("Erro ao salvar a capa: " + nomeArquivoCapa, e);
        }


        //recuperando autor
        Autor autorRecuperado = autorService.findByNome(autor);

        // Criando o objeto Livro e atribuindo as informações
        Livro livro = new Livro();
        livro.setTitulo(titulo);
        livro.setAutor(autorRecuperado);
        livro.setDescricao(descricao);
        livro.setCaminhoArquivo(nomeArquivoPdf);  // Salvando o nome do arquivo PDF no banco
        livro.setCaminhoCapa(nomeArquivoCapa);
        Categoria categoria = categoriaRepository.findByGeneroIgnoreCase(nomeCategoria);
        livro.setCategoria(categoria);
        livro.setClassificacaoIndicativa(classificacaoIndicativa);


        // Salvando o livro no repositório
        livroRepository.save(livro);

        return livro;
    }

    // ... (métodos deletarLivro, atualizarLivro, findById, findByTitulo, findAll permanecem os mesmos) ...
    public String deletarLivro(Long id){
        Livro livro = livroRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Não existe livro com esse id"));

        // Excluir o arquivo físico PDF
        if (livro.getCaminhoArquivo() != null && !livro.getCaminhoArquivo().isEmpty()) {
            try {
                Path pdfPath = this.rootLocation.resolve(livro.getCaminhoArquivo()).normalize();
                Files.deleteIfExists(pdfPath);
            } catch (IOException e) {
                // Logar o erro, mas continuar para excluir do banco de dados
                System.err.println("Erro ao deletar arquivo PDF físico: " + livro.getCaminhoArquivo() + " - " + e.getMessage());
            }
        }
        // Excluir a capa do diretório de capas
        if (livro.getCaminhoCapa() != null && !livro.getCaminhoCapa().isEmpty()) {
            try {
                Path capaPath = this.capasLocation.resolve(livro.getCaminhoCapa()).normalize();
                Files.deleteIfExists(capaPath);
            } catch (IOException e) {
                // Logar o erro, mas continuar para excluir do banco de dados
                System.err.println("Erro ao deletar arquivo de capa físico: " + livro.getCaminhoCapa() + " - " + e.getMessage());
            }
        }

        livroRepository.delete(livro);
        return "Livro de id = " + id + " foi deletado!";
    }

    public ResponseEntity<Livro> atualizarLivro(MultipartFile pdf, MultipartFile capa, String titulo, String autor, String descricao, Long id, String categoria, ClassificacaoIndicativa classificacaoIndicativa) {
        Livro livroAntigo = livroRepository.findById(id).orElseThrow(() -> new RuntimeException("Não existe livro com o id passado"));

        // Atualiza o arquivo PDF se um novo foi enviado
        if (pdf != null && !pdf.isEmpty()) {
            // Valida o tipo do novo PDF
            String tipoNovoPdf = pdf.getContentType();
            if (!"application/pdf".equals(tipoNovoPdf)) {
                throw new IllegalArgumentException("O novo arquivo precisa ser um PDF.");
            }

            // Excluir o arquivo PDF antigo
            if (livroAntigo.getCaminhoArquivo() != null && !livroAntigo.getCaminhoArquivo().isEmpty()) {
                try {
                    Path pdfAntigoPath = this.rootLocation.resolve(livroAntigo.getCaminhoArquivo()).normalize();
                    Files.deleteIfExists(pdfAntigoPath);
                } catch (IOException e) {
                    System.err.println("Erro ao deletar arquivo PDF antigo: " + livroAntigo.getCaminhoArquivo() + " - " + e.getMessage());
                }
            }

            // Salvar o novo arquivo PDF
            String nomeNovoPdf = UUID.randomUUID().toString() + "-" + pdf.getOriginalFilename();
            try {
                Path destinoNovoPdf = this.rootLocation.resolve(nomeNovoPdf);
                Files.copy(pdf.getInputStream(), destinoNovoPdf, StandardCopyOption.REPLACE_EXISTING);
                livroAntigo.setCaminhoArquivo(nomeNovoPdf);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao salvar o novo arquivo PDF: " + nomeNovoPdf, e);
            }
        }

        // Atualiza a capa se uma nova foi enviada
        if (capa != null && !capa.isEmpty()) {
            // Valida o tipo da nova capa
            String tipoNovaCapa = capa.getContentType();
            if (tipoNovaCapa == null || (!tipoNovaCapa.equals("image/jpeg") && !tipoNovaCapa.equals("image/png"))) {
                throw new IllegalArgumentException("A nova capa precisa ser um PNG ou JPEG.");
            }
            // Excluir a capa antiga
            if (livroAntigo.getCaminhoCapa() != null && !livroAntigo.getCaminhoCapa().isEmpty()) {
                try {
                    Path capaAntigaPath = this.capasLocation.resolve(livroAntigo.getCaminhoCapa()).normalize();
                    Files.deleteIfExists(capaAntigaPath);
                } catch (IOException e) {
                    System.err.println("Erro ao deletar arquivo de capa antigo: " + livroAntigo.getCaminhoCapa() + " - " + e.getMessage());
                }
            }

            // Salvar a nova capa
            String nomeNovaCapa = UUID.randomUUID().toString() + "-" + capa.getOriginalFilename();
            try {
                Path destinoNovaCapa = this.capasLocation.resolve(nomeNovaCapa);
                Files.copy(capa.getInputStream(), destinoNovaCapa, StandardCopyOption.REPLACE_EXISTING);
                livroAntigo.setCaminhoCapa(nomeNovaCapa);
            } catch (IOException e) {
                throw new RuntimeException("Erro ao salvar a nova capa: " + nomeNovaCapa, e);
            }
        }

        //recuperando a categoria
        Categoria cat = categoriaRepository.findByGeneroIgnoreCase(categoria);

        //recuperando autor
        Autor autorRecuperado = autorService.findByNome(autor);

        livroAntigo.setTitulo(titulo);
        livroAntigo.setAutor(autorRecuperado);
        livroAntigo.setDescricao(descricao);
        livroAntigo.setCategoria(cat);
        livroAntigo.setClassificacaoIndicativa(classificacaoIndicativa);
        

        return ResponseEntity.ok(livroRepository.save(livroAntigo));
    }


    public ResponseEntity<Livro> findById(long id){
        Livro livro = livroRepository.findById(id).orElseThrow(() -> new RuntimeException("Não existe um livro com esse id"));
        return ResponseEntity.ok(livro);
    }

    public Livro findByTitulo(String titulo){
        Livro livro = livroRepository.findByTitulo(titulo);
        if (livro == null) {
            // Você pode querer lançar uma exceção aqui se o livro não for encontrado,
            // dependendo de como o controller que chama este método espera lidar com isso.
            // Por exemplo: throw new RuntimeException("Livro com título '" + titulo + "' não encontrado.");
            // Ou simplesmente retornar null e deixar o controller tratar.
        }
        return livro;
    }

    @Autowired
    private UserService userService; // ou injete como preferir

    public ResponseEntity<List<Livro>> findAll() {
        // Primeiro: BUSQUE o usuário logado
        User usuario = userService.getUsuarioLogado(); // ou pegue conforme seu contexto

        // Se usuário nulo, pode lançar exceção ou devolver vazio
        if (usuario == null || usuario.getDataNascimento() == null) {
            return ResponseEntity.badRequest().build(); // ou algum tratamento seu
        }

        // Calcula idade do usuário
        int idade = calcularIdade(usuario.getDataNascimento(), LocalDate.now());

        // Busca todos os livros
        List<Livro> todosLivros = livroRepository.findAll();

        // Filtra livros de acordo com a classificação
        List<Livro> permitidos = todosLivros.stream()
            .filter(livro -> podeAcessarLivro(idade, livro.getClassificacaoIndicativa()))
            .collect(Collectors.toList());

        return ResponseEntity.ok(permitidos);
    }


    public Resource downloadByName(String titulo) throws MalformedURLException {
        Livro livro = livroRepository.findByTitulo(titulo);
        if (livro == null || livro.getCaminhoArquivo() == null || livro.getCaminhoArquivo().isEmpty()) {
            throw new RuntimeException("Arquivo PDF não encontrado para o livro: " + titulo);
        }

        Path caminhoArquivo = this.rootLocation.resolve(livro.getCaminhoArquivo()).normalize();
        Resource resource = new UrlResource(caminhoArquivo.toUri());

        if (!resource.exists() || !resource.isReadable()) {
            throw new RuntimeException("Arquivo PDF não encontrado ou não pode ser lido: " + livro.getCaminhoArquivo());
        }

        return resource;
    }

    // --- NOVO MÉTODO PARA CARREGAR A CAPA COMO RESOURCE ---
    public Resource carregarCapaComoResource(String titulo) throws MalformedURLException {
        Livro livro = livroRepository.findByTitulo(titulo);

        if (livro == null) {
            // Log ou throw: Livro com título '{}' não encontrado , titulo
            throw new RuntimeException("Livro não encontrado com o título: " + titulo);
        }

        if (livro.getCaminhoCapa() == null || livro.getCaminhoCapa().isEmpty()) {
            // Log ou throw: Livro '{}' não possui caminho de capa definido , titulo
            throw new RuntimeException("Capa não definida para o livro: " + titulo);
        }

        try {
            Path caminhoCapa = this.capasLocation.resolve(livro.getCaminhoCapa()).normalize();
            Resource resource = new UrlResource(caminhoCapa.toUri());

            if (resource.exists() && resource.isReadable()) {
                return resource;
            } else {
                // Log: Recurso da capa não existe ou não é legível em: {} , caminhoCapa.toString()
                throw new RuntimeException("Capa não encontrada ou não pode ser lida: " + livro.getCaminhoCapa());
            }
        } catch (MalformedURLException e) {
            // Log: URL malformada para a capa '{}' do livro '{}': {} , livro.getCaminhoCapa(), titulo, e.getMessage()
            throw new RuntimeException("Erro ao carregar a capa (URL malformada): " + livro.getCaminhoCapa(), e);
        }
    }


    public void deletarLivroByTitulo(String titulo){ // Este método parece não estar sendo usado pelo controller
        Livro livro = livroRepository.findByTitulo(titulo);
        if (livro == null) {
            throw new RuntimeException("Livro com título '" + titulo + "' não encontrado para deleção.");
        }
        Path caminho = rootLocation.resolve(livro.getCaminhoArquivo());
        try{
            Files.deleteIfExists(caminho);
        } catch (IOException e) {
            throw new RuntimeException("Erro ao deletar arquivo PDF: " + livro.getCaminhoArquivo(), e);
        }

        livroRepository.deleteById(livro.getId());
    }

    public ResponseEntity<Livro> findByTituloPesquisa(String titulo){
        Livro livro = livroRepository.findByTitulo(titulo);
        if(livro == null){
            throw new RuntimeException("o livro é nulo");
        }
        return ResponseEntity.ok().body(livroRepository.findByTitulo(titulo));
    }

    public List<Livro> getLivrosByIdCategoria(long idCategoria){
        return livroRepository.findByCategoriaId(idCategoria);
    }

    public Page<Livro> getLivrosPaginationCategoria(Long idCategoria, int page){
        PageRequest pageRequest = PageRequest.of(page, 5);
        return livroRepository.getLivrosByCategoria(idCategoria, pageRequest);
    }

    public List<Livro> listarLivrosPermitidosParaUsuario(User usuario) {
        int idade = calcularIdade(usuario.getDataNascimento(), LocalDate.now());
        List<Livro> todosLivros = livroRepository.findAll();
        return todosLivros.stream()
                .filter(livro -> podeAcessarLivro(idade, livro.getClassificacaoIndicativa()))
                .collect(Collectors.toList());
    }

    private int calcularIdade(LocalDate nascimento, LocalDate hoje) {
        if (nascimento == null) return 0;
        return Period.between(nascimento, hoje).getYears();
    }

    private boolean podeAcessarLivro(int idade, ClassificacaoIndicativa classificacao) {
        // Se não tem classificação, libera. Se tem, avalia pela idade mínima:
        if (classificacao == null) return true;
        return idade >= classificacao.getIdadeMinima();
    }

    public int calcularIdadeHelper(LocalDate nascimento, LocalDate hoje) {
        if (nascimento == null) return 0;
        return java.time.Period.between(nascimento, hoje).getYears();
    }

    public boolean podeAcessarLivroHelper(int idade, com.example.trabalho_biblioteca.model.ClassificacaoIndicativa classificacao) {
        if (classificacao == null) return true;
        return idade >= classificacao.getIdadeMinima();
    }

    public User getUsuarioLogado() {
        return userService.getUsuarioLogado();
    }

    public List<Livro> getLivrosByAutor(String autor){
        return livroRepository.getLivrosByAutor(autor);
    }

}
