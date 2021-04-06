package io.github.vagnereix.agenda.model.api.rest;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.Part;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import io.github.vagnereix.agenda.model.entity.Contato;
import io.github.vagnereix.agenda.model.repository.ContatoRepository;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/contatos")
public class ContatoController {
	private final ContatoRepository repository;
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Contato save(@RequestBody Contato contato) {
		return repository.save(contato);
	}
	
	@DeleteMapping("{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable Integer id) {
		repository.deleteById(id);
	}
	
	@GetMapping
	public Page<Contato> list(
			@RequestParam(value = "page", defaultValue = "0") Integer pagina,
			@RequestParam(value = "size", defaultValue = "5") Integer tamanhoPagina
	){
		Sort sort = Sort.by(Sort.Direction.ASC, "nome"); //ordenando em ordem alfabética
		PageRequest pageRequest = PageRequest.of(pagina, tamanhoPagina, sort);
		return repository.findAll(pageRequest); //retornando uma lista paginada de contatos
	}
	
	@PatchMapping("{id}/favorito") //patch é utilizado para atualização parcial no objeto - uma única propriedade
	public void favorite(@PathVariable Integer id) {
		Optional<Contato> contato = repository.findById(id);
		contato.ifPresent(c -> {
			boolean favorito = c.getFavorito() == Boolean.TRUE; //retorna true se não for favorito
			c.setFavorito(!favorito);
			repository.save(c);
		});
	}
	
	@PatchMapping("{id}/foto")
	private byte[] addPhoto(@PathVariable Integer id, @RequestParam("foto") Part arquivo ) {
		Optional<Contato> contato = repository.findById(id);
		return contato.map(c -> {
			try {
				InputStream is = arquivo.getInputStream();
				byte[] bytes = new byte[(int) arquivo.getSize()]; //cria array de bytes do tamanho arquivo
				IOUtils.readFully(is, bytes);
				c.setFoto(bytes);
				repository.save(c);
				return bytes;
			}catch (IOException e){
				return null;
			}
		}).orElse(null);
	}
}
