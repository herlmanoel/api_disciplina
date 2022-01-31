package com.api_curso.applications.controllers;

import com.api_curso.applications.interfaces.BaseController;
import com.api_curso.domain.entities.Aluno;
import com.api_curso.domain.entities.Disciplina;
import com.api_curso.domain.entities.Professor;
import com.api_curso.domain.error.types.EntityNotFoundException;
import com.api_curso.domain.repositories.AlunoRepository;
import com.api_curso.domain.repositories.DisciplinaRepository;
import com.api_curso.domain.repositories.ProfessorRepository;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * Responsável por controlar as rotas do endpoint (/disciplinas) e realizar a regra de negócio
 * @author Herlmaneol Fernandes Barbosa
 * @see com.api_curso.applications.interfaces.BaseController
 */
@RestController
@RequestMapping("/disciplinas")
public class DisciplinaController implements BaseController<Disciplina> {
    @Autowired
    private DisciplinaRepository disciplinaRepository;
    @Autowired
    private ProfessorRepository professorRepository;
    @Autowired
    private AlunoRepository alunoRepository;

    @Override
    public ResponseEntity<Object> findAll() {
        List<Disciplina> disciplinas = disciplinaRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(disciplinas);
    }

    @Override
    public ResponseEntity<Object> findOne(Long id) {
        Disciplina disciplina = disciplinaRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Disciplina.class.getSimpleName()));

        return ResponseEntity.status(HttpStatus.OK).body(disciplina);
    }

    @Override
    public ResponseEntity<Object> save(Disciplina request)  {

        if(disciplinaRepository.findByNome(request.getNome()).isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Já existe uma disciplina com esse nome.");
        }

        Optional<Disciplina> disciplina = Optional.of(disciplinaRepository.save(request));

        if(!disciplina.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Não foi possível salvar");
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(disciplina.get());
    }

    @Override
    public ResponseEntity<Object> update(Long id, Disciplina request) {
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Disciplina.class.getSimpleName()));

        BeanUtils.copyProperties(request, disciplina, "id");
        Optional<Disciplina> disciplinaUpdated = Optional.of(disciplinaRepository.save(disciplina));

        if(!disciplinaUpdated.isPresent()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("Não foi possível salvar");
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(disciplinaUpdated.get());
    }

    @Override
    public ResponseEntity<Object> remove(Long id) {
        Disciplina disciplina = disciplinaRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Disciplina.class.getSimpleName()));

        disciplinaRepository.delete(disciplina);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body("elemento removido.");
    }

    @PutMapping("/associarProfessor/{disciplinaId}/{professorId}")
    public ResponseEntity<Object> associateProfessor(@PathVariable Long disciplinaId, @PathVariable Long professorId) {
        Disciplina disciplina = disciplinaRepository.findById(disciplinaId)
                .orElseThrow(() -> new EntityNotFoundException(Disciplina.class.getSimpleName()));

        Professor professor = professorRepository.findById(professorId)
                .orElseThrow(() -> new EntityNotFoundException(Professor.class.getSimpleName()));

        disciplina.setProfessor(professor);
        disciplinaRepository.save(disciplina);

        return ResponseEntity.status(HttpStatus.CREATED).body(disciplina);
    }

    @PutMapping("/associarAluno/{disciplinaId}/{alunoId}")
    public ResponseEntity<Object> associateAluno(@PathVariable Long disciplinaId, @PathVariable Long alunoId) {
        Disciplina disciplina = disciplinaRepository
                .findById(disciplinaId)
                .orElseThrow(() -> new EntityNotFoundException(Disciplina.class.getSimpleName()));
        Aluno aluno = alunoRepository
                .findById(alunoId)
                .orElseThrow(() -> new EntityNotFoundException(Aluno.class.getSimpleName()));
        disciplina.addAluno(aluno);
        disciplinaRepository.save(disciplina);
        return ResponseEntity.status(HttpStatus.OK).body(disciplina);
    }

    @GetMapping("/{id}/alunos")
    public ResponseEntity<Object> listAlunos(@PathVariable Long id) {
        Disciplina disciplina = disciplinaRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Disciplina.class.getSimpleName()));
        Set<Aluno> alunos = disciplina.getAlunos();
        return ResponseEntity.status(HttpStatus.OK).body(alunos);
    }

    @GetMapping("/{id}/professor")
    public ResponseEntity<Object> getProfessor(@PathVariable Long id) {
        Disciplina disciplina = disciplinaRepository
                .findById(id)
                .orElseThrow(() -> new EntityNotFoundException(Disciplina.class.getSimpleName()));
        Optional<Professor> professor = Optional.of(disciplina.getProfessor());
        return ResponseEntity.status(HttpStatus.OK).body(professor);
    }
}
