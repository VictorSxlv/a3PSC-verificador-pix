package com.a3psc.pixriscoapi.repository;

import com.a3psc.pixriscoapi.model.Chave; //importando entidade chave
import org.springframework.data.jpa.repository.JpaRepository; //métodos prontos pra mexer com o banco
import org.springframework.stereotype.Repository; //diz que onde vem @Repository do Spring
import java.util.Optional; //biblioteca pra chamar o Optional
@Repository

public interface ChaveRepository extends JpaRepository<Chave, Integer> {
    Optional<Chave> findBychavepix(String chavepix); // Optional diz que talvez a busca retorne "null" (chave não econtrada)
}
