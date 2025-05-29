package com.a3psc.pixriscoapi.repository;

import com.a3psc.pixriscoapi.model.Conta; //importando entidade conta
import org.springframework.data.jpa.repository.JpaRepository; //métodos prontos pra mexer com o banco
import org.springframework.stereotype.Repository; //diz que onde vem @Repository do Spring
@Repository

public interface ContaRepository extends JpaRepository<Conta, Integer> {
}
