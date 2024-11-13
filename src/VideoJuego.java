import java.util.*;
class Soldado {
    private int nivelVida;
    private int nivelAtaque;
    public Soldado() {
        Random ran = new Random();
        this.nivelVida = ran.nextInt(5) + 1;
        this.nivelAtaque = ran.nextInt(5) + 1;
    }
    public int getNivelVida() {
        return nivelVida;
    }
    public int getNivelAtaque() {
        return nivelAtaque;
    }
    public void bonusTerritorio(String territorio) {
        switch (territorio) {
            case "bosque":
            case "campoAbierto":
            case "montaña":
            case "desierto":
            case "playa":
                nivelVida++;
                break;
        }
    }
}
class Ejercito {
    private ArrayList<Soldado> soldados;
    private String reino;
    public Ejercito(String reino) {
        Random ran = new Random();
        this.reino = reino;
        int cantidadSoldados = ran.nextInt(10) + 1;
        this.soldados = new ArrayList<>();
        for (int i = 0; i < cantidadSoldados; i++) {
            soldados.add(new Soldado());
        }
    }
    public String getReino() {
        return reino;
    }
    public ArrayList<Soldado> getSoldados() {
        return soldados;
    }
    public int totalVida() {
        int totalVida = 0;
        for (Soldado s : soldados) {
            totalVida += s.getNivelVida();
        }
        return totalVida;
    }
    public int totalAtaque() {
        int totalAtaque = 0;
        for (Soldado s : soldados) {
            totalAtaque += s.getNivelAtaque();
        }
        return totalAtaque;
    }
    public int sumaVidaYataque() {
        return totalVida() + totalAtaque();
    }
    public String getInfoEjercito() {
        return soldados.size() + "-" + totalVida() + "-" + this.reino.charAt(0);
    }
}
class Mapa {
    private String[][] tablero;
    private int filas;
    private int columnas;
    private ArrayList<Ejercito> ejercitos;
    public Mapa(int filas, int columnas, String[] reinosSeleccionados) {
        this.filas = filas;
        this.columnas = columnas;
        this.tablero = new String[filas][columnas];
        this.ejercitos = new ArrayList<>();
        crearEjercitos(reinosSeleccionados);//crea ejercitos y los coloca
    }
    private void crearEjercitos(String[] reinosSeleccionados) { // crea ejercito del reino selcccionado
        Random ran = new Random();
        for (String reino : reinosSeleccionados) {
            int cantidadEjercitos = ran.nextInt(10) + 1;
            for (int i = 0; i < cantidadEjercitos; i++) {
                Ejercito ejercito = new Ejercito(reino);
                ejercitos.add(ejercito);
                posicionarEjercito(ejercito); //se coloca en el tablero
            }
        }
    }
    private void posicionarEjercito(Ejercito ejercito) {
        Random ran = new Random();
        int x, y;
        do {
            x = ran.nextInt(filas);
            y = ran.nextInt(columnas);
        } while (tablero[x][y] != null && !tablero[x][y].equals(""));       //evitar dos ejercitos en el mismo lugar
        String territorio = asignarTerritorio(y, ejercito.getReino());
        for (Soldado s : ejercito.getSoldados()) {
            s.bonusTerritorio(territorio);  //bono segun el territorio
        }
        tablero[x][y] = ejercito.getInfoEjercito(); //coloca al ejercito en la casilla
    }
    private String asignarTerritorio(int columna, String reino) {//tipo de territorio segun colummna
        if ((reino.equals("Inglaterra") && columna < 2) ||
                (reino.equals("Francia") && columna >= 2 && columna < 4) ||
                (reino.equals("Castilla-Aragón") && columna >= 4 && columna < 6) ||
                (reino.equals("Moros") && columna >= 6 && columna < 8)) {
            if (reino.equals("Inglaterra"))
                return "bosque";
            if (reino.equals("Francia"))
                return "campoAbierto";
            if (reino.equals("Castilla-Aragon"))
                return "montaña";
            if (reino.equals("Moros"))
                return "desierto";
        }
        return "playa";  //Sacro Imperio Romano-Germanico
    }
    public void mostrarTablero() {
        for (int i = 0; i < filas; i++) {
            System.out.print("|");
            for (int j = 0; j < columnas; j++) {
                if (tablero[i][j] == null || tablero[i][j].equals("")) {
                    System.out.print("_____|");
                } else {
                    System.out.print(tablero[i][j] + "|");
                }
            }
            System.out.println();
        }
    }
    public ArrayList<Ejercito> getEjercitos() {
        return ejercitos;
    }
    public String[][] getTablero() {
        return tablero;
    }
    public int getFilas() {
        return filas;
    }
    public int getColumnas() {
        return columnas;
    }
    public boolean verificarMovimiento(int x, int y, String reino) {
        if (tablero[x][y] == null || tablero[x][y].equals("")) {
            return true;  //la casilla esta vacia
        }
        String contenido = tablero[x][y];
        String reinoEnPosicion = contenido.substring(contenido.indexOf("-") + 1, contenido.indexOf("-") + 2);
        return !reinoEnPosicion.equals(reino.substring(0, 1));  // No permitir que un ejército se mueva a una casilla ocupada por su propio reino
    }
    public boolean moverEjercito(int x, int y, int nuevaX, int nuevaY) {
        if (!verificarMovimiento(nuevaX, nuevaY, tablero[x][y].substring(0, 1))) {
            return false;         //movimiento no valido
        }
        String contenido = tablero[nuevaX][nuevaY];  //si al mover a esa casilla hay otro se realiza batalla
        if (contenido != null && !contenido.equals("") && !contenido.substring(0, 1).equals(tablero[x][y].substring(0, 1))) {
            String reino = tablero[x][y].substring(1, 2);  //pobtener el reino del ejercito actual
            realizarBatalla(x, y, reino);
        }
        tablero[nuevaX][nuevaY] = tablero[x][y]; //mover al ejercito a la casilla
        tablero[x][y] = "";//la casilla deja en blanco
        return true; //realiza correctamente el movimiento
    }
    public void realizarBatalla(int x, int y, String reino) {
        String contenido = tablero[x][y];
        String reinoRival = contenido.substring(contenido.indexOf("-") + 1, contenido.indexOf("-") + 2);
        Ejercito ejercitoRival = null;
        Ejercito ejercitoActual = null;
        for (Ejercito e : ejercitos) {
            if (e.getReino().equals(reinoRival)) {
                ejercitoRival = e;
            }
            if (e.getReino().equals(reino)) {
                ejercitoActual = e;
            }
        }
        if (ejercitoRival == null || ejercitoActual == null) {
            System.out.println("No se pudo encontrar los ejércitos.");
            return;
        }
        int vidaActual = ejercitoActual.totalVida();
        int ataqueActual = ejercitoActual.totalAtaque();
        int vidaRival = ejercitoRival.totalVida();
        int ataqueRival = ejercitoRival.totalAtaque();
        double probabilidadActual = (double) (vidaActual + ataqueActual) / (vidaActual + ataqueActual + vidaRival + ataqueRival);
        double probabilidadRival = 1 - probabilidadActual;
        System.out.println("Probabilidad de victoria de " + reino + ": " + (probabilidadActual * 100) + "%");
        System.out.println("Probabilidad de victoria de " + reinoRival + ": " + (probabilidadRival * 100) + "%");
        Random rand = new Random();
        if (rand.nextDouble() < probabilidadActual) {
            System.out.println(reino + " gana la batalla con una probabilidad de " + (probabilidadActual * 100) + "%");
            for (Soldado s : ejercitoActual.getSoldados()) { //ejercito actual ocupa la casilla
                s.bonusTerritorio("bono"); //aumento de vida por bono
            }
        } else {
            System.out.println(reinoRival + " gana la batalla con una probabilidad de " + (probabilidadRival * 100) + "%");
            for (Soldado s : ejercitoRival.getSoldados()) { //ejercito rival ocupa la casilla
                s.bonusTerritorio("bono");
            }
        }
    }
}
public class VideoJuego {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        String[] opcionesReinos = {"Inglaterra", "Francia", "Castilla-Aragón", "Moros", "Sacro Imperio Romano-Germánico"};
        String[] reinosSeleccionados = new String[2];
        System.out.println("Seleccione los dos reinos para jugar (1 para Inglaterra, 2 para Francia, 3 para Castilla-Aragón, 4 para Moros, 5 para Sacro Imperio Romano-Germánico):");
        for (int i = 0; i < 2; i++) {
            System.out.println("Elija el reino #" + (i + 1) + ":");
            int eleccion = sc.nextInt();
            while (eleccion < 1 || eleccion > 5) {
                System.out.println("Opción inválida. Por favor elija un número entre 1 y 5.");
                eleccion = sc.nextInt();
            }
            reinosSeleccionados[i] = opcionesReinos[eleccion - 1];
        }
        Mapa mapa = new Mapa(10, 10, reinosSeleccionados);
        while (true) {
            mapa.mostrarTablero();
            for (String reino : reinosSeleccionados) {
                System.out.println("Es el turno de " + reino);
                jugadaJugador(reino, mapa);

                mapa.mostrarTablero();
            }
        }
    }
    public static void jugadaJugador(String reino, Mapa mapa) {
        Scanner sc = new Scanner(System.in);
        int x = -1, y = -1;
        boolean posicionValida = false;
        while (!posicionValida) {
            System.out.println("Ingrese las coordenadas (x, y) de su ejército a mover:");
            x = sc.nextInt();
            y = sc.nextInt();
            if (x < 0 || x >= mapa.getFilas() || y < 0 || y >= mapa.getColumnas()) {
                System.out.println("Coordenadas fuera de los límites del mapa. Intente nuevamente.");
                continue;
            }
            String contenido = mapa.getTablero()[x][y];
            if (contenido != null && !contenido.equals("") && contenido.contains(reino.substring(0, 1))) {
                posicionValida = true;  //ejercito pertenece al reino del jugador
            } else {
                System.out.println("No se pudo encontrar el ejército de " + reino + " en esa posición. Intente de nuevo.");
            }
        }
        System.out.println("Ingrese la dirección (N, S, E, O):");
        char direccionChar = sc.next().toUpperCase().charAt(0);
        int nuevaX = x, nuevaY = y;
        switch (direccionChar) {
            case 'N':
                nuevaX--;
                break;
            case 'S':
                nuevaX++;
                break;
            case 'E':
                nuevaY++;
                break;
            case 'O':
                nuevaY--;
                break;
            default:
                System.out.println("Dirección no válida. Intente de nuevo.");
                jugadaJugador(reino, mapa);
                return;
        }
        if (mapa.moverEjercito(x, y, nuevaX, nuevaY)) {
            System.out.println("Movimiento realizado.");
        } else {
            System.out.println("Movimiento no válido. Intente de nuevo.");
            jugadaJugador(reino, mapa);
        }
    }
}