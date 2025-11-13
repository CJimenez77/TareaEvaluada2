package com.evaluacion.tareaevaluada2.Servicio;

import com.evaluacion.tareaevaluada2.Modelo.Variante;
import com.evaluacion.tareaevaluada2.Repositorio.VarianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class VarianteService {

    @Autowired
    private VarianteRepository varianteRepository;

    public Variante crearVariante(Variante variante) {
        return varianteRepository.save(variante);
    }

    public List<Variante> listarVariantes() {
        return varianteRepository.findAll();
    }
}