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
    private Soldado[] soldados;
    private String reino;
    public Ejercito(String reino) {
        Random ran = new Random();
        this.reino = reino;
        int cantidadSoldados = ran.nextInt(10) + 1; // Entre 1 y 10 soldados
        this.soldados = new Soldado[cantidadSoldados];
        for (int i = 0; i < cantidadSoldados; i++) {
            soldados[i] = new Soldado();
        }
    }
    public String getReino() {
        return reino;
    }
    public Soldado[] getSoldados() {
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
        return soldados.length + "-" + totalVida() + "-" + this.reino.charAt(0);
    }
}
class Mapa {
    private String[][] tablero;
    private int filas;
    private int columnas;
    private Ejercito[] ejercitos;
    public Mapa(int filas, int columnas) {
        this.filas = filas;
        this.columnas = columnas;
        this.tablero = new String[filas][columnas];
        this.ejercitos = new Ejercito[50];   //maximo de ejercitos por reinos
        crearEjercitos();
    }
    private void crearEjercitos() {
        Random ran = new Random();
        String[] reinos = {"Inglaterra", "Francia", "Castilla-Aragón", "Moros", "Sacro Imperio Romano-Germánico"};
        int index = 0;
        for (String reino : reinos) {
            int cantidadEjercitos = ran.nextInt(10) + 1;
            for (int i = 0; i < cantidadEjercitos; i++) {
                Ejercito ejercito = new Ejercito(reino);
                ejercitos[index++] = ejercito;
                posicionarEjercito(ejercito);
            }
        }
    }
    private void posicionarEjercito(Ejercito ejercito) {
        Random ran = new Random();
        int x, y;
        do {
            x = ran.nextInt(filas);
            y = ran.nextInt(columnas);
        } while (tablero[x][y] != null && !tablero[x][y].equals("")); //evitar dos ejercitos en el mismo lugar
        String territorio = asignarTerritorio(y, ejercito.getReino());//asignamos territorio segun columnas
        for (Soldado s : ejercito.getSoldados()) {//aplicar bonus segun territorio
            s.bonusTerritorio(territorio);
        }
        tablero[x][y] = ejercito.getInfoEjercito();//se coloca el ejercito en la celda
    }
    private String asignarTerritorio(int columna, String reino) {
        if ((reino.equals("Inglaterra") && columna < 2) || (reino.equals("Francia") && columna >= 2 && columna < 4) ||
                (reino.equals("Castilla-Aragón") && columna >= 4 && columna < 6) || (reino.equals("Moros") && columna >= 6 && columna < 8)) {
            if (reino.equals("Inglaterra") )
                return "bosque";
            if (reino.equals("Francia"))
                return "campoAbierto";
            if (reino.equals("Castilla-Aragón"))
                return "montaña";
            if (reino.equals("Moros"))
                return "desierto";
        }
        return "playa"; // Para el Sacro Imperio Romano-Germánico en el caso de que esté en las últimas columnas
    }
    public Ejercito[] getEjercitos() {
        return ejercitos;
    }
    public void mostrarTablero() {
        for (int i = 0; i < filas; i++) {
            System.out.print("|");
            for (int j = 0; j < columnas; j++) {
                if (tablero[i][j] == null || tablero[i][j].equals("")) {
                    System.out.print("_____|");
                } else {
                    String cellContent = tablero[i][j];
                    System.out.print(cellContent + "|");
                }
            }
            System.out.println();
        }
    }
}
public class VideoJuego {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        boolean seguirJugando = true;
        while (seguirJugando) {
            Mapa mapa = new Mapa(10, 10);
            System.out.println("Tablero de batallas:");
            mapa.mostrarTablero();
            System.out.println("\nElija la métrica para determinar el ganador:");
            System.out.println("1. Mayor nivel de vida total");
            System.out.println("2. Suma de vida + ataque total");
            System.out.println("3. Mayor suma de ataque");
            System.out.print("Seleccione una opción (1/2/3): ");
            int opcion = sc.nextInt();
            batalla(mapa, opcion);//batalla segun metrica elegida
            System.out.print("\n¿Quieres jugar otra vez? (s/n): ");
            char respuesta = sc.next().charAt(0);
            if (respuesta == 'n' || respuesta == 'N') {
                seguirJugando = false;
            }
        }
    }
    public static void batalla(Mapa mapa, int opcion) {
        Ejercito[] ejercitos = mapa.getEjercitos();
        Ejercito ganador = null;
        switch (opcion) {
            case 1: //metrica 1: mayor nivel de vida total
                int maxVida = -1;
                for (Ejercito ejercito : ejercitos) {
                    if (ejercito != null) {
                        int vidaTotal = ejercito.totalVida();
                        if (vidaTotal > maxVida) {
                            maxVida = vidaTotal;
                            ganador = ejercito;
                        }
                    }
                }
                break;

            case 2: //metrica 2: suma total de vida y ataque
                int maxSumaVidaAtaque = -1;
                for (Ejercito ejercito : ejercitos) {
                    if (ejercito != null) {
                        int sumaVidaAtaque = ejercito.sumaVidaYataque();
                        if (sumaVidaAtaque > maxSumaVidaAtaque) {
                            maxSumaVidaAtaque = sumaVidaAtaque;
                            ganador = ejercito;
                        }
                    }
                }
                break;

            case 3: //metrica 3: mayor suma de ataque
                int maxAtaque = -1;
                for (Ejercito ejercito : ejercitos) {
                    if (ejercito != null) {
                        int ataqueTotal = ejercito.totalAtaque();
                        if (ataqueTotal > maxAtaque) {
                            maxAtaque = ataqueTotal;
                            ganador = ejercito;
                        }
                    }
                }
                break;
            default:
                System.out.println("Opción no válida.");
                return;
        }
        if (ganador != null) {
            System.out.println(ganador.getReino() + " gana la guerra según la métrica seleccionada.");
        }
    }
}