package com.salud.citas;

import com.salud.citas.application.port.in.CitaUseCase;
import com.salud.citas.application.port.in.PagoUseCase;
import com.salud.citas.application.port.in.ProfesionalUseCase;
import com.salud.citas.application.port.in.UsuarioUseCase;
import com.salud.citas.application.port.out.CitaRepositoryPort;
import com.salud.citas.application.port.out.EspecialidadRepositoryPort;
import com.salud.citas.application.port.out.PagoRepositoryPort;
import com.salud.citas.application.port.out.PasarelaPagoPort;
import com.salud.citas.application.port.out.ProfesionalRepositoryPort;
import com.salud.citas.application.port.out.UsuarioRepositoryPort;
import com.salud.citas.application.service.CitaService;
import com.salud.citas.application.service.PagoService;
import com.salud.citas.application.service.ProfesionalService;
import com.salud.citas.application.service.UsuarioService;
import com.salud.citas.infrastructure.adapter.in.view.CitaView;
import com.salud.citas.infrastructure.adapter.in.view.LectorConsola;
import com.salud.citas.infrastructure.adapter.in.view.MenuPrincipalView;
import com.salud.citas.infrastructure.adapter.in.view.PagoView;
import com.salud.citas.infrastructure.adapter.in.view.SelectorCita;
import com.salud.citas.infrastructure.adapter.in.view.UsuarioView;
import com.salud.citas.infrastructure.adapter.out.pasarela.PasarelaPagoSimulada;
import com.salud.citas.infrastructure.adapter.out.repository.CitaRepositoryMemoria;
import com.salud.citas.infrastructure.adapter.out.repository.EspecialidadRepositoryMemoria;
import com.salud.citas.infrastructure.adapter.out.repository.PagoRepositoryMemoria;
import com.salud.citas.infrastructure.adapter.out.repository.ProfesionalRepositoryMemoria;
import com.salud.citas.infrastructure.adapter.out.repository.UsuarioRepositoryMemoria;

/**
 * Punto de entrada. Aquí se "arma" todo: se crean las implementaciones,
 * se inyectan en los services (por constructor) y los services en las vistas.
 */
public class Main {

    public static void main(String[] args) {
        // 1. Puertos de salida -> adaptadores (repositories con ArrayList y pasarela simulada)
        UsuarioRepositoryPort usuarioRepo = new UsuarioRepositoryMemoria();
        CitaRepositoryPort citaRepo = new CitaRepositoryMemoria();
        PagoRepositoryPort pagoRepo = new PagoRepositoryMemoria();
        EspecialidadRepositoryPort especialidadRepo = new EspecialidadRepositoryMemoria();
        ProfesionalRepositoryPort profesionalRepo = new ProfesionalRepositoryMemoria(especialidadRepo);
        PasarelaPagoPort pasarela = new PasarelaPagoSimulada();

        // 2. Puertos de entrada -> services (reciben solo interfaces)
        UsuarioUseCase usuarios = new UsuarioService(usuarioRepo);
        CitaUseCase citas = new CitaService(citaRepo, usuarioRepo, profesionalRepo);
        PagoUseCase pagos = new PagoService(citaRepo, pagoRepo, pasarela);
        ProfesionalUseCase profesionales = new ProfesionalService(profesionalRepo, especialidadRepo, citaRepo);

        // 3. Vistas (reciben los casos de uso, no los services concretos)
        LectorConsola lector = new LectorConsola();
        SelectorCita selector = new SelectorCita(citas, lector);
        UsuarioView usuarioView = new UsuarioView(usuarios, lector);
        CitaView citaView = new CitaView(citas, profesionales, selector, lector);
        PagoView pagoView = new PagoView(pagos, selector, lector);

        // 4. Arrancar el menú
        new MenuPrincipalView(usuarioView, citaView, pagoView, lector).iniciar();
    }
}
