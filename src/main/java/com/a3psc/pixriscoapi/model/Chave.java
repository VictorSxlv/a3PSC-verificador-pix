package com.a3psc.pixriscoapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

@Entity
@Table(name = "chave")
@Getter
@Setter
public class Chave {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(nullable = false, unique = true)
    private String chavepix;

    @Enumerated(EnumType.STRING) // Armazena o ENUM como String no banco (senão ficaria ordinal)
    @Column(name = "tipo", nullable = false, updatable = false)
    private TipoChave tipo;

    @Column(nullable = false)
    private LocalDateTime datacriacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contaid", nullable = false)
    private Conta conta;

}