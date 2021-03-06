package com.api_curso.domain.entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Disciplina extends BaseEntity {

    private String nome;

    @Column(columnDefinition="TEXT")
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "professor_id")
    private Professor professor;

    @ManyToMany
    @JoinTable(name = "disciplina_aluno",
            joinColumns = @JoinColumn(name = "disciplina_id"),
            inverseJoinColumns = @JoinColumn(name = "aluno_id"))
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private Set<Aluno> alunos = new HashSet<>();

    public Boolean addAluno(Aluno aluno){
        return this.alunos.add(aluno);
    }

    public void addAlunos(Set<Aluno> alunos){
        alunos.forEach(aluno -> this.alunos.add(aluno));
    }

    public Boolean removeAluno(Aluno aluno) {
        return this.alunos.remove(aluno);
    }


}
