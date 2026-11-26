package br.com.senai.estoque;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
public class ProdutoController {

    private final ProdutoRepository repository;

    public ProdutoController(ProdutoRepository repository) {
        this.repository = repository;
    }

    @GetMapping("/")
    public String listarProdutos(@RequestParam(value = "busca", required = false) String busca, Model model) {
        List<Produto> todosProdutos = repository.findAll();
        List<Produto> listaProdutos;

        if (busca != null && !busca.trim().isEmpty()) {
            listaProdutos = repository.findByNomeContainingIgnoreCase(busca);
        } else {
            listaProdutos = todosProdutos;
        }

        double valorTotalGeral = todosProdutos.stream()
                .mapToDouble(Produto::calcularValorTotalEstoque)
                .sum();

        model.addAttribute("produtos", listaProdutos);
        model.addAttribute("haProdutos", !todosProdutos.isEmpty());
        model.addAttribute("novoProduto", new Produto());
        model.addAttribute("busca", busca);
        model.addAttribute("valorTotalGeral", valorTotalGeral);

        return "index";
    }

    @PostMapping("/salvar")
    public String salvarProduto(@ModelAttribute Produto produto) {
        repository.save(produto);
        return "redirect:/";
    }

    @PostMapping("/adicionar/{id}")
    public String adicionarUnidade(@PathVariable Long id,
                                   @RequestParam(required = false) String busca,
                                   RedirectAttributes redirectAttributes) {
        Produto produto = repository.findById(id).orElse(null);
        if (produto != null) {
            produto.adicionarEstoque(1);
            repository.save(produto);
        }
        preservarBusca(busca, redirectAttributes);
        return "redirect:/";
    }

    @PostMapping("/remover/{id}")
    public String removerUnidade(@PathVariable Long id,
                                 @RequestParam(required = false) String busca,
                                 RedirectAttributes redirectAttributes) {
        Produto produto = repository.findById(id).orElse(null);
        if (produto != null) {
            produto.removerEstoque(1);
            repository.save(produto);
        }
        preservarBusca(busca, redirectAttributes);
        return "redirect:/";
    }

    @PostMapping("/excluir/{id}")
    public String excluirProduto(@PathVariable Long id,
                                 @RequestParam(required = false) String busca,
                                 RedirectAttributes redirectAttributes) {
        repository.deleteById(id);
        preservarBusca(busca, redirectAttributes);
        return "redirect:/";
    }

    private void preservarBusca(String busca, RedirectAttributes redirectAttributes) {
        if (busca != null && !busca.isBlank()) {
            redirectAttributes.addAttribute("busca", busca);
        }
    }
}
