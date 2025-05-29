package com.a3psc.pixriscoapi.repository;

import com.a3psc.pixriscoapi.model.Chave; //importando entidade chave
import com.a3psc.pixriscoapi.model.Denuncia; //importando entidade denúncia
import org.springframework.data.jpa.repository.JpaRepository; //métodos prontos pra mexer com o banco
import org.springframework.stereotype.Repository; //diz que onde vem @Repository do Spring
import java.util.List; //biblioteca pra chamar o List
@Repository

public interface DenunciaRepository extends JpaRepository<Denuncia, Integer> {
    List<Denuncia> findBychave(Chave chave); //Pede uma lista como retorno, pois pode ter mais de 2 denúncia
    long countBychave(Chave chave);
}

