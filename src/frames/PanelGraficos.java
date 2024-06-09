package frames;

import graficos.Cubo3D;
import java.awt.Color;
import java.awt.Graphics;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import Interfaces.LabelManager;
import graficos.Objeto3D;
import java.util.ArrayList;
import javax.swing.JLabel;
import javax.swing.SwingConstants;
import utils.Constantes;

public class PanelGraficos extends JPanel implements Runnable, LabelManager {

    private Thread hiloPanelGraficos;
    private static final ArrayList<JLabel> listaTagLabels;

    private static final JLabel labelInfoControles;
    private static final JLabel labelInfoObjeto;
    private static final JLabel labelInfoControlesPersistente;
    private static final JLabel labelInfoObjetoPersistente;

    private double[] puntoFuga = {450, 300, 250};

    private Objeto3D objetoActual;
    private ArrayList<Objeto3D> listaCubos = new ArrayList<>();
    private int currentIndex = 0;

    static {
        listaTagLabels = new ArrayList<>();

        labelInfoControles = new JLabel("<html>--------------------- CONTROLES ---------------------<br><br>"
                + "ESPACIO -> Parar/Reanudar la animacion<br>"
                + "TAB -> Alternar traslacion/Rotacion<br>"
                + "SCROLL -> Aumentar/Disminuir la escala<br>"
                + "IZQUIERDA -> Anterior objeto<br>"
                + "DERECHA -> Siguiente objeto<br><br>"
                + "Click Izq -> Rotacion (Ejes activados)<br>"
                + "Click Der -> Traslacion (X e Y)<br><br>"
                + "W -> Transformar para arriba<br>"
                + "A -> Transformar para la izquierda<br>"
                + "S -> Transformar para abajo<br>"
                + "D -> Transformar para la derecha<br>"
                + "Q -> Transformar para Z negativo<br>"
                + "E -> Transformar para Z positivo<br><br>"
                + "Z -> Activar/Desactivar Puntos<br>"
                + "X -> Activar/Desactivar Lineas<br>"
                + "C -> Activar/Desactivar Caras<br><br>"
                + "1 -> Activar/Desactivar Eje X<br>"
                + "2 -> Activar/Desactivar Eje Y<br>"
                + "3 -> Activar/Desactivar Eje Z<br>"
                + "</html>");

        labelInfoObjeto = new JLabel("<html><div style='text-align: right;'>------------------- INFORMACION -------------------<br><br>"
                + "ID OBJETO: #1<br>"
                + "FPS: 60<br><br>"
                + "Puntos: Visibles<br>"
                + "Lineas: Visibles<br>"
                + "Caras: Invisibles<br><br>"
                + "Punto de origen:<br>"
                + "X -> 450 pixeles<br>"
                + "Y -> 300 pixeles<br>"
                + "Z -> 700 pixeles<br><br>"
                + "Punto de fuga:<br>"
                + "X -> 450 pixeles<br>"
                + "Y -> 300 pixeles<br>"
                + "Z -> 250 pixeles<br>"
                + "FOV -> 250 pixeles<br><br>"
                + "Ejes activos:<br>"
                + "X -> Activado<br>"
                + "Y -> Activado<br>"
                + "Z -> Desactivado<br><br>"
                + "</div></html>");

        labelInfoControlesPersistente = new JLabel("<html>CTRL -> Ocultar/Mostrar controles<br></html>");
        labelInfoObjetoPersistente = new JLabel("<html>ALT -> Ocultar/Mostrar informacion<br></html>");
    }

    public PanelGraficos() {
        SwingUtilities.invokeLater(() -> {
            initComponentes();
            this.setBackground(new Color(38, 38, 38));
            this.setLayout(null);
            this.setFocusable(true);
            this.requestFocus();
            this.requestFocusInWindow();

            double[] origenCubo = {450, 300, 700};
            Cubo3D cubo = new Cubo3D(getWidth(), getHeight(), origenCubo, puntoFuga, this);
            listaCubos.add(cubo);

            double[] origenCubo2 = {200, 300, 700};
            Cubo3D cubo2 = new Cubo3D(getWidth(), getHeight(), origenCubo2, puntoFuga, this);
            listaCubos.add(cubo2);

            double[] origenCubo3 = {700, 300, 700};
            Cubo3D cubo3 = new Cubo3D(getWidth(), getHeight(), origenCubo3, puntoFuga, this);
            listaCubos.add(cubo3);

            if (!listaCubos.isEmpty()) {
                objetoActual = listaCubos.get(currentIndex);
                objetoActual.setSeleccionado(true);
            }

            this.hiloPanelGraficos = new Thread(this);
            this.hiloPanelGraficos.start();
        });
    }

    public void initComponentes() {
        labelInfoControles.setForeground(Color.WHITE);
        labelInfoControles.setVerticalAlignment(SwingConstants.TOP);
        labelInfoControles.setBounds(-250, 20, 250, 575);
        add(labelInfoControles);

        labelInfoControlesPersistente.setForeground(Color.WHITE);
        labelInfoControlesPersistente.setBounds(20, 570, 250, 10);
        add(labelInfoControlesPersistente);

        labelInfoObjeto.setForeground(Color.WHITE);
        labelInfoObjeto.setVerticalAlignment(SwingConstants.TOP);
        labelInfoObjeto.setHorizontalAlignment(SwingConstants.TRAILING);
        labelInfoObjeto.setBounds(905, 20, 250, 575);
        add(labelInfoObjeto);

        labelInfoObjetoPersistente.setForeground(Color.WHITE);
        labelInfoObjetoPersistente.setHorizontalAlignment(SwingConstants.TRAILING);
        labelInfoObjetoPersistente.setBounds(625, 570, 250, 10);
        add(labelInfoObjetoPersistente);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);

        listaTagLabels.get(objetoActual.getIdObjeto()).setForeground(new Color(255, 255, 255));
        for (Objeto3D cubo : listaCubos) {
            g.drawImage(cubo.getBuffer(), 0, 0, null);
        }
    }

    public void siguienteElemento() {
        if (currentIndex < listaCubos.size() - 1) {
            objetoActual.iniciarAnimacionDeseleccionado();

            currentIndex++;
            objetoActual = listaCubos.get(currentIndex);
            objetoActual.iniciarAnimacionSeleccionado();
        } else {
            currentIndex = -1;
            siguienteElemento();
        }
    }

    public void anteriorElemento() {
        if (currentIndex > 0) {
            objetoActual.iniciarAnimacionDeseleccionado();
            currentIndex--;
            objetoActual = listaCubos.get(currentIndex);
            objetoActual.iniciarAnimacionSeleccionado();
        } else {
            currentIndex = listaCubos.size();
            anteriorElemento();
        }
    }

    public void setMostrarAnimacion() {
        objetoActual.setMostrarAnimacion();
    }

    public void setEscala(int escala) {
        objetoActual.setEscala(escala);
    }

    public void setRotacionTransformacion() {
        objetoActual.setRotacionTransformacion();
    }

    public void setRotacionTransformacionMouse(int x, int y) {
        objetoActual.setRotacionTransformacionMouse(x, y);
    }

    public void setRotacionTransformacionArriba() {
        objetoActual.setRotacionTransformacionArriba();
    }

    public void setRotacionTransformacionAbajo() {
        objetoActual.setRotacionTransformacionAbajo();
    }

    public void setRotacionTransformacionIzquierda() {
        objetoActual.setRotacionTransformacionIzquierda();
    }

    public void setRotacionTransformacionDerecha() {
        objetoActual.setRotacionTransformacionDerecha();
    }

    public void setRotacionTransformacionZPositiva() {
        objetoActual.setRotacionTransformacionZPositiva();
    }

    public void setRotacionTransformacionZNegativa() {
        objetoActual.setRotacionTransformacionZNegativa();
    }

    public void setMostrarPuntos() {
        objetoActual.setMostrarPuntos();
    }

    public void setMostrarLineas() {
        objetoActual.setMostrarLineas();
    }

    public void setMostrarCaras() {
        objetoActual.setMostrarCaras();
    }

    public void setEjeXAnimacion() {
        objetoActual.setEjeXAnimacion();
    }

    public void setEjeYAnimacion() {
        objetoActual.setEjeYAnimacion();
    }

    public void setEjeZAnimacion() {
        objetoActual.setEjeZAnimacion();
    }

    public void trasladarCubos(int d) {
        for (Objeto3D cubo : listaCubos) {
            cubo.trasladarX(d);
        }
    }

    public void ocultarControles() {
        Thread thread = new Thread(() -> {
            int tempX = labelInfoControles.getX();

            while (tempX > -labelInfoControles.getWidth()) {
                tempX -= 10;
                trasladarCubos(10);
                labelInfoControles.setLocation(tempX, labelInfoControles.getY());
                try {
                    Thread.sleep(16);
                } catch (InterruptedException ex) {
                    Logger.getLogger(FrameAnimacion.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        thread.start();
    }

    public void ocultarInformacion() {
        Thread thread = new Thread(() -> {
            int tempX = labelInfoObjeto.getX();
            int panelWidth = getWidth();

            while (tempX < panelWidth) {
                tempX += 10;
                trasladarCubos(-10);
                labelInfoObjeto.setLocation(tempX, labelInfoObjeto.getY());
                try {
                    Thread.sleep(16);
                } catch (InterruptedException ex) {
                    Logger.getLogger(FrameAnimacion.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        thread.start();
    }

    public void mostrarControles() {
        Thread thread = new Thread(() -> {
            int tempX = labelInfoControles.getX();

            while (tempX < 20) {
                tempX += 10;
                trasladarCubos(-10);
                labelInfoControles.setLocation(tempX, labelInfoControles.getY());
                try {
                    Thread.sleep(16);
                } catch (InterruptedException ex) {
                    Logger.getLogger(FrameAnimacion.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        thread.start();
    }

    public void mostrarInformacion() {
        Thread thread = new Thread(() -> {
            int tempX = labelInfoObjeto.getX();

            while (tempX > 625) {
                tempX -= 10;
                trasladarCubos(10);
                labelInfoObjeto.setLocation(tempX, labelInfoObjeto.getY());
                try {
                    Thread.sleep(16);
                } catch (InterruptedException ex) {
                    Logger.getLogger(FrameAnimacion.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        thread.start();
    }

    @Override
    public void run() {
        while (true) {
            repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException ex) {
                Logger.getLogger(PanelGraficos.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void aniadirEtiqueta(JLabel tagLabel, int x, int y) {
        JLabel tempLabel2 = tagLabel;
        tempLabel2.setHorizontalAlignment(SwingConstants.TRAILING);
        tempLabel2.setBounds(x, y, Constantes.TAG_LABEL_WIDTH, Constantes.TAG_LABEL_HEIGHT);
        tempLabel2.setForeground(new Color(38, 38, 38));
        listaTagLabels.add(tempLabel2);
        add(tempLabel2);
    }

    @Override
    public void actualizarEtiquetaInformacion(int indice, String texto) {
        labelInfoObjeto.setText(texto);
    }

    @Override
    public void actualizarEtiquetaObjeto(int indice, int x, int y) {
        listaTagLabels.get(indice).setLocation(x, y);
    }
}
