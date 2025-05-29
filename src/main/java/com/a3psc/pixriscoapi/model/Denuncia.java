package com.a3psc.pixriscoapi.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDateTime;

@Entity
@Table(name = "denuncia")
@Getter
@Setter
public class Denuncia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "chaveid", nullable = false)
    private Chave chave;

    @Column(nullable = false)
    private LocalDateTime datahora;

    @Column(nullable = false)
    private Double confiab;
}