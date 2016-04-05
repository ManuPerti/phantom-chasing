
import java.io.File;
import org.lwjgl.LWJGLUtil;
import org.newdawn.slick.*;
import org.newdawn.slick.Input.*;
import org.newdawn.slick.font.effects.ColorEffect;
import org.newdawn.slick.tiled.*;

/**
 * Código inicial para la tarea 2
 *
 */
/**
 *
 * @author Manuel Pertíñez Sánchez
 */
public class Tarea02 extends BasicGame {
    //Declaración de variables globales.

    // Tilemap.
    private TiledMap mapa;

    //Dimensiones del mapa y los tiles.
    private int mapaWidth, mapaHeight;
    private int totalTilesWidth, totalTilesHeight;
    private int tileWidth, tileHeight;

    //Vectores para almacenar los obstáculos, los huecos y la muralla.
    private boolean[][] obstaculo, agujero, muralla;

    // Declaración y animaciones del jugador.
    private Personaje jugador;
    private SpriteSheet cuadrosJugador;
    private Animation jugadorArriba;
    private Animation jugadorDerecha;
    private Animation jugadorAbajo;
    private Animation jugadorIzquierda;

    // Declaración y animaciones del fantasma1.
    private Npc fantasma1 = null, fantasma2 = null, fantasma3 = null, fantasma4 = null;
    private SpriteSheet cuadrosFantasmas;

    // Variable global que incrementa la velocidad del fantasma1.
    private float incremento;

    private int inventario;

    //Objetos de la clase Tesoro, que son los que debe recuperar el jugador.
    Tesoro espada, armadura, escudo, casco;

    // Escritura de cadenas.
    private UnicodeFont fuente;

    // Música y sonidos.
    private Music musica;
    private Sound sonidoCapturado;
    private Sound sonidoCogeHerramienta;
    private Sound sonidoCaerAgujero;
    private Sound sonidoAplausos;

    // Variable que marca el fin del juego.
    private boolean fin = false;

    public Tarea02(String name) {
        super(name);
    }

    public static void main(String[] args) {
        System.setProperty("org.lwjgl.librarypath", new File(new File(System.getProperty("user.dir"), "native"), LWJGLUtil.getPlatformName()).getAbsolutePath());
        System.setProperty("net.java.games.input.librarypath", System.getProperty("org.lwjgl.librarypath"));
        try {
            AppGameContainer container = new AppGameContainer(new Tarea02("PMDM02 - Tarea"));
            container.setDisplayMode(640, 480, true);
            container.setTargetFrameRate(60);
            container.setVSync(true);
            container.setShowFPS(false);
            container.setUpdateOnlyWhenVisible(false);
            container.start();
        } catch (SlickException e) {
        }

    }

    /**
     * Definición de la clase Sprite que permite crear un objeto con
     * animaciones.
     */
    public class Sprite {

        protected SpriteSheet cuadros;
        protected Animation spriteArriba;
        protected Animation spriteAbajo;
        protected Animation spriteIzquierda;
        protected Animation spriteDerecha;
        protected Animation sprite;
        protected int anchura;
        protected int altura;
        protected float posX;
        protected float posY;

        public Sprite(String path, int anchura, int altura, Animation spriteUp,
                Animation spriteDown, Animation spriteLeft, Animation spriteRight,
                float posX, float posY) throws SlickException {

            cuadros = new SpriteSheet(path, anchura, altura);
            spriteArriba = spriteUp;
            spriteAbajo = spriteDown;
            spriteIzquierda = spriteLeft;
            spriteDerecha = spriteRight;
            sprite = spriteAbajo;
            this.anchura = anchura;
            this.altura = altura;
            this.posX = posX;
            this.posY = posY;
        }

        public Animation getSprite() {
            return sprite;
        }

        public void setSprite(Animation sprite) {
            this.sprite = sprite;
        }

        public float getPosX() {
            return posX;
        }

        public void setPosX(float posX) {
            this.posX = posX;
        }

        public float getPosY() {
            return posY;
        }

        public void setPosY(float posY) {
            this.posY = posY;
        }

        public int getAnchura() {
            return anchura;
        }

        public int getAltura() {
            return altura;
        }

        public boolean chocaObstaculo(boolean[][] lista) {
            boolean choque = false;
            if (getPosX() > (mapaWidth - getAnchura())
                    || (lista[(int) (getPosX() / tileWidth)][((int) (getPosY() + getAltura()) / tileHeight)])
                    || (lista[(int) ((getPosX() + getAnchura()) / tileWidth)][((int) (getPosY() + getAltura()) / tileHeight)])) {
                choque = true;
            }
            return choque;
        }
    }// class Sprite

    /**
     * Clase derivada de Sprite que permite crear un personaje controlable por
     * el jugador.
     */
    public class Personaje extends Sprite {

        protected boolean vivo;
        protected boolean ganador;
        protected boolean caido;
        protected boolean capturado;

        public Personaje(String path, int anchura, int altura, Animation spriteUp,
                Animation spriteDown, Animation spriteLeft, Animation spriteRight,
                float posX, float posY, boolean vivo, boolean ganador,
                boolean caido, boolean capturado) throws SlickException {

            super(path, anchura, altura, spriteUp, spriteDown, spriteLeft,
                    spriteRight, posX, posY);
            this.vivo = vivo;
            this.ganador = ganador;
            this.caido = caido;
            this.capturado = capturado;

        }

        public boolean isVivo() {
            return vivo;
        }

        public void setVivo(boolean vivo) {
            this.vivo = vivo;
        }

        public boolean isGanador() {
            return ganador;
        }

        public void setGanador(boolean ganador) {
            this.ganador = ganador;
        }

        public boolean isCaido() {
            return caido;
        }

        public void setCaido(boolean caido) {
            this.caido = caido;
        }

        public boolean isCapturado() {
            return capturado;
        }

        public void setCapturado(boolean capturado) {
            this.capturado = capturado;
        }

        public boolean caidaAgujero(boolean[][] lista) {
            boolean agujero = false;
            if ((lista[(int) (jugador.getPosX() / tileWidth)][((int) (jugador.getPosY() + jugador.getAltura()) / tileHeight)])
                    || (lista[(int) ((jugador.getPosX() + jugador.getAnchura()) / tileWidth)][((int) (jugador.getPosY() + jugador.getAltura()) / tileHeight)])) {
                agujero = true;
            }
            return agujero;
        }
    }// class Personaje

    /**
     * Clase derivad de Sprite que permite crear un NPC, es decir un personaje
     * no controlable por el jugador que se regirá por una IA o patrón de
     * comportamiento.
     */
    public class Npc extends Sprite {

        protected float velocidad;
        protected float[] vectorVelocidad = new float[2];

        public Npc(String path, int anchura, int altura, Animation spriteUp,
                Animation spriteDown, Animation spriteLeft, Animation spriteRight,
                float posX, float posY, float velocidad, float[] vectorVelocidad)
                throws SlickException {

            super(path, anchura, altura, spriteUp, spriteDown, spriteLeft,
                    spriteRight, posX, posY);

            this.velocidad = velocidad;
            this.vectorVelocidad = vectorVelocidad;

        }

        public float getVelocidad() {
            return velocidad;
        }

        public void setVelocidad(float velocidad) {
            this.velocidad = velocidad;
        }

        public float[] getVectorVelocidad() {
            return vectorVelocidad;
        }

        public void setVectorVelocidad(float v0, float v1) {
            vectorVelocidad[0] = v0;
            vectorVelocidad[1] = v1;
        }

        public void setVectorX(float x) {
            vectorVelocidad[0] = x;
        }

        public void setVectorY(float y) {
            vectorVelocidad[1] = y;
        }

        /**
         * Método para que el NPC persiga al jugador continuamente siguiendo una
         * línea recta.
         *
         * @param jugadorX
         * @param jugadorY
         */
        public void perseguir(float jugadorX, float jugadorY) {
            float v0 = jugadorX - getPosX();
            float v1 = jugadorY - getPosY();

            float modulo = (float) Math.sqrt(v0 * v0 + v1 * v1);
            setVectorVelocidad(v0 / modulo, v1 / modulo);
            escoge_animacion(v0, v1);

            setPosX(getPosX() + getVelocidad() * getVectorVelocidad()[0] * incremento);
            setPosY(getPosY() + getVelocidad() * getVectorVelocidad()[1] * incremento);

        }

        /**
         * Método que escoge la animación correcta del Sprite a partir de los
         * valores del vector velocidad.
         *
         * @param v0
         * @param v1
         */
        public void escoge_animacion(float v0, float v1) {
            if (v1 > 0) {
                if (v0 > 0) {
                    setSprite(spriteAbajo);
                    if (v0 > v1) {
                        setSprite(spriteDerecha);
                    }
                } else {
                    if (-v0 > v1) {
                        setSprite(spriteIzquierda);
                    }
                }
            } else {
                if (v0 > 0) {
                    setSprite(spriteArriba);
                    if (v0 > -v1) {
                        setSprite(spriteDerecha);
                    }
                } else {
                    if (v0 < v1) {
                        setSprite(spriteIzquierda);
                    }
                }
            }
        }

        public boolean colision(Sprite objeto) {
            boolean choque = false;
            if ((getPosX() - getAnchura() / 2 < objeto.getPosX() + objeto.getAnchura() / 2)
                    && (getPosX() + getAnchura() / 2 > objeto.getPosX() - objeto.getAnchura() / 2)
                    && (getPosY() + getAltura() / 2 > objeto.getPosY() - objeto.getAltura() / 2)
                    && (getPosY() - getAltura() / 2 < objeto.getPosY() + objeto.getAltura() / 2)) {
                choque = true;
            }
            return choque;
        }

    }// class Npc

    public Npc creaFantasma() throws SlickException {

        Animation fantasmaAbajo = new Animation(cuadrosFantasmas, 0, 0, 3, 0, true, 150, true);
        Animation fantasmaIzquierda = new Animation(cuadrosFantasmas, 0, 1, 3, 1, true, 150, true);
        Animation fantasmaDerecha = new Animation(cuadrosFantasmas, 0, 2, 3, 2, true, 150, true);
        Animation fantasmaArriba = new Animation(cuadrosFantasmas, 0, 3, 3, 3, true, 150, true);

        float[] vector = {0, 0};
        Npc fantasma = new Npc("data/ghost.png", 25, 26, fantasmaArriba, fantasmaAbajo,
                fantasmaIzquierda, fantasmaDerecha, mapaWidth / 2, mapaHeight / 2 - 150f,
                0.5f, vector);

        //fantasma.setVelocidad(0.5f);//Asignamos una velocidad al fantasma1.
        fantasma.setVectorVelocidad(0, 0);
        fantasma.setSprite(fantasmaAbajo);

        return fantasma;
    }

    /**
     * Clase que representa los tesoros que el jugador debe recoger.
     */
    public class Tesoro {

        protected Image icono;
        protected float posX;
        protected float posY;
        protected boolean recogido = false;

        /**
         * Método constructor de la clase Tesoro.
         *
         * @param path
         * @param posX
         * @param posY
         * @throws SlickException
         */
        public Tesoro(String path, float posX, float posY) throws SlickException {
            icono = new Image(path);
            this.posX = posX;
            this.posY = posY;
        }

        /**
         * Método para determinar si el jugador recoge un tesoro.
         *
         * @param jugadorX
         * @param jugadorY
         * @return
         */
        public boolean recoger(float jugadorX, float jugadorY) {
            if (jugadorX >= this.posX - tileWidth / 2
                    && jugadorX <= this.posX + tileWidth / 2
                    && jugadorY >= this.posY - tileHeight / 2
                    && jugadorY <= this.posY + tileHeight / 2
                    && isRecogido() == false) {
                setRecogido(true);
                sonidoCogeHerramienta.play();
                incremento += 0.1;
                inventario += 1;
            }
            return isRecogido();
        }

        /**
         * Método que cambia la posición de un tesoro.
         *
         * @param posX
         * @param posY
         */
        public void setPosicion(float posX, float posY) {
            this.posX = posX;
            this.posY = posY;
        }

        /**
         * Método que devuelve la imagen del tesoro.
         *
         * @return
         */
        public Image getIcono() {
            return icono;
        }

        /**
         * Método que establece si el tesoro ha sido recogido o no.
         *
         * @param recogido
         */
        public void setRecogido(boolean recogido) {
            this.recogido = recogido;
        }

        /**
         * Método que devuelve si el tesoro si el tesoro ha sido recogido o no.
         *
         * @return
         */
        public boolean isRecogido() {
            return recogido;
        }
    }// class Tesoro

    // Método para iniciar todas las variables del juego.
    public void iniciar_juego() throws SlickException {

        //Situamos el jugador en su posición inicial.
        jugador.setPosX(mapaWidth / 2);
        jugador.setPosY(mapaHeight - 65f);
        jugador.setVivo(true); //Indicamos que está vivo inicialmente.
        jugador.setGanador(false); //Indicamos que no ha ganado desde el comienzo.
        jugador.setSprite(jugadorAbajo);

        fantasma1 = creaFantasma();
        fantasma2 = null;
        fantasma3 = null;
        fantasma4 = null;
        fantasma1.setPosX((float) (Math.random() * 330 + 40));
        fantasma1.setPosY((float) (Math.random() * 200 + 40));

        // El incremento de velocidad se inicia en 1.
        incremento = 1;

        // Objetos recogidos, iniciado en 0.
        inventario = 0;

        // Colocamos los objetos en su posición inicial y sin ser recogidos por
        // el jugador.
        espada.setPosicion(35, 90);
        armadura.setPosicion(30, 355);
        escudo.setPosicion(608, 96);
        casco.setPosicion(544, 288);

        espada.setRecogido(false);
        armadura.setRecogido(false);
        escudo.setRecogido(false);
        casco.setRecogido(false);

        musica.loop(); // Hacemos que se repita la música de fondo.
        fin = false;
        jugador.setCaido(false);
        jugador.setCapturado(false);

    }// método iniciar_juego

    // En este método cargamos todas los archivos necesarios para iniciar el juego:
    //
    @Override
    public void init(GameContainer gc) throws SlickException {

        // Cargamos el mapa y obtenemos sus dimensiones
        mapa = new TiledMap("data/mapa_tarea02.tmx", "data");
        tileWidth = mapa.getTileWidth();
        tileHeight = mapa.getTileHeight();
        totalTilesWidth = mapa.getWidth();
        totalTilesHeight = mapa.getHeight();
        mapaWidth = totalTilesWidth * tileWidth;
        mapaHeight = totalTilesHeight * tileHeight;
        
        // Cargamos los spritesheets del jugador (caballero) y los fantasmas.
        cuadrosJugador = new SpriteSheet("data/mosquetero.png", 24, 32);
        cuadrosFantasmas = new SpriteSheet("data/ghost.png", 25, 26);

        // Cargamos las animaciones del jugador.
        jugadorArriba = new Animation(cuadrosJugador, 0, 0, 2, 0, true, 150, false);
        jugadorDerecha = new Animation(cuadrosJugador, 0, 1, 2, 1, true, 150, false);
        jugadorAbajo = new Animation(cuadrosJugador, 0, 2, 2, 2, true, 150, false);
        jugadorIzquierda = new Animation(cuadrosJugador, 0, 3, 2, 3, true, 150, false);

        // Creamos el personaje mediante el constructor.
        jugador = new Personaje("data/mosquetero.png", 24, 32, jugadorArriba, jugadorAbajo,
                jugadorIzquierda, jugadorDerecha, mapaWidth / 2, mapaHeight - 65f,
                false, false, false, false);

        // Cargamos las imágenes de los Sprites que debe recuperar el jugador.
        espada = new Tesoro("data/espada.png", 35, 90);
        armadura = new Tesoro("data/armadura.png", 30, 355);
        escudo = new Tesoro("data/escudo.jpg", 608, 96);
        casco = new Tesoro("data/casco.png", 544, 288);

        // Recorremos todo los tiles del plano y vamos almacenando en el vector
        // correspondiente las coordenadas de aquellos que correspondan con
        // obstáculo, agujero o muralla respectivamente.
        obstaculo = new boolean[totalTilesWidth][totalTilesHeight];
        agujero = new boolean[totalTilesWidth][totalTilesHeight];
        muralla = new boolean[totalTilesWidth][totalTilesHeight];

        for (int x = 0; x < totalTilesWidth; x++) {
            for (int y = 0; y < totalTilesHeight; y++) {
                obstaculo[x][y] = (mapa.getTileId(x, y, 3) != 0)
                        || (mapa.getTileId(x, y, 5) != 0);
                agujero[x][y] = mapa.getTileId(x, y, 4) != 0;
                muralla[x][y] = mapa.getTileId(x, y, 5) != 0;
            }
        }

        /**
         * *********************************************************************
         */
        // Cargar tipo de letra de la carpeta data y todos los símbolos
        // que podamos necesitar:        
        fuente = new UnicodeFont("data/tuffy.ttf", 28, false, false);
        // Añade las letras ASCII estánndar
        fuente.addAsciiGlyphs();
        // y ahora añadimos los caracteres españoles
        fuente.addGlyphs("Ã¡Ã©Ã­Ã³ÃºÃÃ‰ÃÃ“ÃšÃ±Ã‘Â¡Â¿");
        // en Slick es obligatorio añadir un efecto para poder dibujar
        // texto. Añadimos un efecto vacío.
        fuente.getEffects().add(new ColorEffect(java.awt.Color.WHITE));
        // cargamos los símbolos del tipo de letra
        fuente.loadGlyphs();
        // A partir de ahora, llamando a fuente.drawString(x, y, texto) podremos
        // escribir en el contenedor

        // Añadimos música de fondo
        musica = new Music("data/tuturne land!.mod");

        // Cargamos los efectos de sonido que necesitaremos reproducir según la
        // situación.
        sonidoCapturado = new Sound("data/game_over.ogg");  // Cuando el jugador es capturado por el fantasma1
        sonidoCogeHerramienta = new Sound("data/coin-object.ogg"); // Cuando el jugador recoge cada uno de los objetos.
        sonidoCaerAgujero = new Sound("data/object-which-falls-fake.ogg"); // Cuando el jugador cae por alguno de los agujeros.
        sonidoAplausos = new Sound("data/success-1.ogg"); // Cuando el jugador ha ganado la partida.

        iniciar_juego();
    }// método init

    // Método para actualizar el valor de las variables del juego.
    @Override
    public void update(GameContainer gc, int delta) throws SlickException {

        boolean paused = false;
        Input entrada = gc.getInput();
        float jugadorAnteriorX = jugador.getPosX();
        float jugadorAnteriorY = jugador.getPosY();
        float fantasma1AnteriorX = fantasma1.getPosX();
        float fantasma1AnteriorY = fantasma1.getPosY();

        // Si se pulsa Esc, salimos del juego.
        if (entrada.isKeyDown(Input.KEY_ESCAPE)) {	// Tecla ESC
            gc.exit();
        }

        //Manejadores de eventos para mover el personaje
        if (entrada.isKeyDown(Input.KEY_DOWN)) {     // Tecla abajo
            jugador.setPosY(jugador.getPosY() + delta * 0.1f);
            jugador.setSprite(jugadorAbajo);
            jugador.getSprite().update(delta);
        }
        if (entrada.isKeyDown(Input.KEY_UP)) {     // Tecla arriba
            jugador.setPosY(jugador.getPosY() - delta * 0.1f);
            jugador.setSprite(jugadorArriba);
            jugador.getSprite().update(delta);
        }
        if (entrada.isKeyDown(Input.KEY_RIGHT)) {     // Tecla derecha
            jugador.setPosX(jugador.getPosX() + delta * 0.1f);
            jugador.setSprite(jugadorDerecha);
            jugador.getSprite().update(delta);
        }
        if (entrada.isKeyDown(Input.KEY_LEFT)) {     // Tecla izquierda
            jugador.setPosX(jugador.getPosX() - delta * 0.1f);
            jugador.setSprite(jugadorIzquierda);
            jugador.getSprite().update(delta);
        }

        // Manejador de eventos para iniciar un nuevo juego.
        if (!jugador.isVivo() || jugador.isGanador()) {
            if (entrada.isKeyDown(Input.KEY_S)) {
                iniciar_juego();
            }
            if (entrada.isKeyDown(Input.KEY_N)) {
                gc.exit();
            }
        }

        // Llamamos al método que actualiza la posición del fantasma1 para que
        // persiga continuamente al jugador.
        fantasma1.perseguir(jugador.getPosX(), jugador.getPosY());

        switch (inventario) {
            case 1:
                if (fantasma2 == null) {
                    fantasma2 = creaFantasma();
                }
                break;
            case 2:
                if (fantasma3 == null) {
                    fantasma3 = creaFantasma();
                }
                break;
            case 3:
                if (fantasma4 == null) {
                    fantasma4 = creaFantasma();
                }
                break;

        }

        /* Hacemos que los fantasmas se mantenga en los límites del mapa y que no
         * crucen ninguna muralla.
         */
        if (fantasma1.chocaObstaculo(muralla)) {
            fantasma1.setPosX(fantasma1AnteriorX);
            fantasma1.setPosY(fantasma1AnteriorY);
        }

        if (fantasma2 != null) {
            float fantasma2AnteriorX = fantasma2.getPosX();
            float fantasma2AnteriorY = fantasma2.getPosY();
            fantasma2.perseguir(jugador.getPosX() + 50, jugador.getPosY() + 50);

            if (fantasma2.chocaObstaculo(muralla)) {
                fantasma2.setPosX(fantasma2AnteriorX);
                fantasma2.setPosY(fantasma2AnteriorY);
            }
        }

        if (fantasma3 != null) {
            float fantasma3AnteriorX = fantasma3.getPosX();
            float fantasma3AnteriorY = fantasma3.getPosY();
            fantasma3.perseguir(jugador.getPosX() - 50, jugador.getPosY() - 50);

            if (fantasma3.chocaObstaculo(muralla)) {
                fantasma3.setPosX(fantasma3AnteriorX);
                fantasma3.setPosY(fantasma3AnteriorY);
            }
        }

        if (fantasma4 != null) {
            float fantasma4AnteriorX = fantasma4.getPosX();
            float fantasma4AnteriorY = fantasma4.getPosY();
            fantasma4.perseguir(jugador.getPosX() + 100, jugador.getPosY() + 100);

            if (fantasma4.chocaObstaculo(muralla)) {
                fantasma4.setPosX(fantasma4AnteriorX);
                fantasma4.setPosY(fantasma4AnteriorY);
            }
        }

        /* Actualizamos posición del jugador en los casos en que sea posible y
         * de lo contrario, al chocar con un obstáculo lo dejamos donde estaba.
         */
        if (jugador.chocaObstaculo(obstaculo)) {
            jugador.setPosX(jugadorAnteriorX);
            jugador.setPosY(jugadorAnteriorY);
        }

        // Si el personaje pasa por un agujero o la barca, muere y acaba el juego.
        if (jugador.isVivo()) {
            if (jugador.caidaAgujero(agujero)) {
                jugador.setVivo(false);
                jugador.setCaido(true);
            }
        }

        // Determinamos el caso en que los fantasmas colisionen con el jugador.
        if (jugador.isVivo()) {
            if (fantasma1.colision(jugador)) {
                jugador.setVivo(false);
                jugador.setCapturado(true);
            }
            if (fantasma2 != null) {
                if (fantasma2.colision(jugador)) {
                    jugador.setVivo(false);
                    jugador.setCapturado(true);
                }
            }
            if (fantasma3 != null) {
                if (fantasma3.colision(jugador)) {
                    jugador.setVivo(false);
                    jugador.setCapturado(true);
                }
            }
            if (fantasma4 != null) {
                if (fantasma4.colision(jugador)) {
                    jugador.setVivo(false);
                    jugador.setCapturado(true);
                }
            }
        }

        // Determinamos los casos en que el jugador recoge cada uno de los
        // objetos dispersos por el mapa y los colocamos en su inventario.
        if (jugador.isVivo()) {
            if (espada.recoger(jugador.getPosX(), jugador.getPosY())) {
                espada.setPosicion(350, 0);
            }

            if (armadura.recoger(jugador.getPosX(), jugador.getPosY())) {
                armadura.setPosicion(416, 0);

            }

            if (escudo.recoger(jugador.getPosX(), jugador.getPosY())) {
                escudo.setPosicion(480, 0);
            }

            if (casco.recoger(jugador.getPosX(), jugador.getPosY())) {
                casco.setPosicion(546, 0);
            }
        }

        // Si ha muerto el jugador o ha ganado, ya no se actualiza nada
        if (!jugador.isVivo() || jugador.isGanador()) {
            fin = true;
            musica.stop();
        }

    }// método update

    // Método que dibuja el mapa y los sprites.
    @Override
    public void render(GameContainer gc, Graphics g) throws SlickException {

        // Dibujamos el tilemap capa por capa.
        mapa.render(0, 0, 0); //dibujamos la capa del suelo
        mapa.render(0, 0, 1); //dibujamos la capa de decoración
        mapa.render(0, 0, 2); //dibujamos la segunda capa de decoración
        mapa.render(0, 0, 3); //dibujamos la capa de obstáculos
        mapa.render(0, 0, 4); //dibujamos la capa de agujeros
        mapa.render(0, 0, 5); //dibujamos la capa de muralla

        espada.getIcono().draw(espada.posX, espada.posY, tileWidth, tileHeight);
        armadura.getIcono().draw(armadura.posX, armadura.posY, tileWidth, tileHeight);
        escudo.getIcono().draw(escudo.posX, escudo.posY, tileWidth, tileHeight);
        casco.getIcono().draw(casco.posX, casco.posY, tileWidth, tileHeight);

        // Dibujamos el jugador si está vivo.
        if (!fin) {
            if (jugador.isVivo()) {
                jugador.getSprite().draw(jugador.getPosX(), jugador.getPosY());
            }

            // Dibujamos cada uno de los fantasmas si han sido creados.
            fantasma1.getSprite().draw(fantasma1.getPosX(), fantasma1.getPosY());
            if (fantasma2 != null) {
                fantasma2.getSprite().draw(fantasma2.getPosX(), fantasma2.getPosY());
            }
            if (fantasma3 != null) {
                fantasma3.getSprite().draw(fantasma3.getPosX(), fantasma3.getPosY());
            }
            if (fantasma4 != null) {
                fantasma4.getSprite().draw(fantasma4.getPosX(), fantasma4.getPosY());
            }
        }
        // Pintamos la capa de altura después de dibujar al jugador.
        mapa.render(0, 0, 6);

        //Comprobamos que el jugador haya recogido todos los objetos para indicar
        //el mesaje de que ha ganado.
        if (espada.isRecogido() && armadura.isRecogido() && escudo.isRecogido()
                && casco.isRecogido()) {
            //Si consigue los tres objetos ponemos efecto de sonido una sola vez
            if (!jugador.isGanador()) {
                sonidoAplausos.play();  // Se reproduce este sonido cuando el jugador ha ganado la partida.
            }
            String mensajeGanar = "   Enhorabuena, has ganado.\n ¿Deseas volver a jugar? (s/n)";
            jugador.setGanador(true);
            // dibujamos el texto centrado en el contenedor
            fuente.drawString((gc.getWidth() - fuente.getWidth(mensajeGanar)) / 2, (gc.getHeight()
                    - fuente.getHeight(mensajeGanar)) / 2, mensajeGanar, Color.white);
            return;
        }

        //Si el jugador está muerto
        if (!jugador.isVivo()) {
            String mensajePerder = "   Lo sentimos, has perdido.\n ¿Deseas volver a jugar? (s/n)";
            if (jugador.isCaido() && !sonidoCaerAgujero.playing()) {
                sonidoCaerAgujero.play();
                jugador.setCaido(false);
            }
            if (jugador.isCapturado() && !sonidoCapturado.playing()) {
                sonidoCapturado.play();
                jugador.setCapturado(false);
            }

            // dibujamos el texto centrado en el contenedor
            fuente.drawString((gc.getWidth() - fuente.getWidth(mensajePerder)) / 2, (gc.getHeight()
                    - fuente.getHeight(mensajePerder)) / 2, mensajePerder, Color.white);
        }
    }// método render    

}
