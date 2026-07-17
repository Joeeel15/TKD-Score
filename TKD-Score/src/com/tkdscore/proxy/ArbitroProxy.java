package com.tkdscore.proxy;

// Importamos la interfaz base de las técnicas para registrar los puntos
import com.tkdscore.tecnica.ITecnica;

/**
 * Clase ArbitroProxy que actúa como el Proxy de Seguridad (Protection Proxy).
 * Implementa la interfaz común 'IArbitro'. Su objetivo primordial es interceptar 
 * las llamadas al árbitro real para validar sus credenciales antes de permitirle 
 * iniciar sesión o interactuar con el marcador del torneo.
 */
public class ArbitroProxy implements IArbitro {

    // Referencia interna hacia el objeto real que realiza el trabajo de registro técnico
    private final ArbitroReal arbitroReal;
    
    // Almacena la credencial del árbitro que está intentando operar en el sistema
    private final String credencial;
    
    /**
     * Credencial maestra requerida para que un árbitro pueda interactuar con el sistema.
     * Al estar declarada como 'static final', es una constante compartida a nivel de clase.
     */
    private static final String CREDENCIAL_VALIDA = "WT-2026";

    /**
     * Constructor de ArbitroProxy.
     * Implementa un diseño robusto del tipo 'fail-fast' (fallo inmediato).
     * * @param nombre     Nombre del árbitro que se registrará en el sistema.
     * @param credencial Código o credencial ingresada para validar sus privilegios.
     * @throws AccesoNoAutorizadoException si la credencial no coincide con CREDENCIAL_VALIDA.
     */
    public ArbitroProxy(String nombre, String credencial) {
        this.credencial = credencial;

        /*
         * VALIDACIÓN INMEDIATA (Fail-Fast):
         * Evaluamos la credencial en el constructor. Si es inválida, se interrumpe
         * la creación del objeto lanzando una excepción. Esto evita que el login
         * sea vulnerable y de paso asegura que nadie acceda a las pantallas de control
         * con credenciales falsas o vacías.
         */
        if (!validarCredencial()) {
            throw new AccesoNoAutorizadoException(
                    "Credencial invalida: \"" + credencial + "\" no esta autorizada para ingresar al sistema.");
        }

        // Si la validación fue exitosa, procedemos a instanciar el Árbitro Real
        this.arbitroReal = new ArbitroReal(nombre);
    }

    /**
     * Compara de forma segura si la credencial del usuario coincide con la constante autorizada.
     * * @return true si la credencial es idéntica a "WT-2026", false de lo contrario.
     */
    private boolean validarCredencial() {
        // Usamos equals() llamándolo desde la constante para evitar un posible NullPointerException
        return CREDENCIAL_VALIDA.equals(credencial);
    }

    /**
     * Registra un punto en el combate.
     * Implementa un mecanismo de defensa en profundidad comprobando nuevamente la seguridad
     * antes de delegar la acción de registrar puntajes al objeto de negocio real.
     * * @param atleta  Esquina del atleta que anotó el punto ("AZUL" o "ROJO").
     * @param tecnica Técnica de combate ejecutada con éxito.
     */
    @Override
    public void registrarPunto(String atleta, ITecnica tecnica) {
        /*
         * SEGUNDA VERIFICACIÓN DE SEGURIDAD:
         * Aunque el objeto ya fue construido, volvemos a verificar el token antes de 
         * cada llamada delegada. Esto protege el marcador en caso de que el objeto 
         * proxy sea reutilizado en memoria de manera no autorizada.
         */
        if (!validarCredencial()) {
            throw new AccesoNoAutorizadoException(
                    "Credencial invalida: el arbitro no esta autorizado para modificar el marcador.");
        }
        
        // Delegación de la responsabilidad al Árbitro Real una vez aprobado el filtro
        arbitroReal.registrarPunto(atleta, tecnica);
    }
}