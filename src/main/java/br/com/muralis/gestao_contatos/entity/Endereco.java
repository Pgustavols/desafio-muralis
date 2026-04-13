package br.com.muralis.gestao_contatos.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.*;
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "endereco")
public class Endereco {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "endereco_seq")
    @SequenceGenerator(
            name = "endereco_seq",
            sequenceName = "endereco_sequence",
            allocationSize = 1
    )
    @Setter(AccessLevel.NONE)
    private Long id;

    @OneToOne
    @JoinColumn(name = "cliente_id", unique = true)
    private Cliente cliente;

    @Column(length = 8, nullable = false)
    private String cep;

    @Column(nullable = false)
    private String logradouro;

    @Column(length = 20, nullable = false)
    private String numero;

    private String complemento;

    @Column(length = 100, nullable = false)
    private String bairro;

    @Column(length = 100, nullable = false)
    private String cidade;

    @Column(length = 2, nullable = false)
    private String estado;
}
