package com.a3psc.pixriscoapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "conta")
@Getter
@Setter
public class Conta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // diz pro java que o banco cria o id automaticamente
    private Integer id;

    private String tipo;

    @Column(nullable = false)
    private String instituicao;

    private String agencia;
    private String numeroconta;

    @Column(nullable = false)
    private LocalDateTime datacriacao;

    @ManyToOne(fetch = FetchType.LAZY) // Muitas Contas para um Usuario, e LAZY não carrega info dos users sempre que carrega infos da conta
    @JoinColumn(name = "usuarioid", nullable = false) // Coluna de chave estrangeira
    private Usuario usuario;

    private Integer mediatransacoessemanais;
    private String padraomovimentacoesultimomes;
    private LocalDateTime bloqueadaate;
}