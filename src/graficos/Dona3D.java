package graficos;

import java.awt.Color;
import java.awt.Polygon;
import java.awt.geom.Point2D;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLabel;
import Interfaces.LabelManager;
import java.util.ArrayList;
import utils.Constantes;

public class Dona3D extends Objeto3D implements Runnable {

    private final Thread hiloCubo;

    private int numPuntosActual;

    private double anguloMaximo;
    private double anguloIncremento;
    private double radioMayor;
    private double radioMenor;
    private double stepRadioMenor;
    private boolean decrementando;

    public Dona3D(int frameWidth, int frameHeight, double[] origenCubo, double[] puntoFuga, LabelManager labelManager) {
        super(frameWidth, frameHeight, origenCubo, puntoFuga, labelManager);
        this.numPuntosActual = numPuntos;
        this.escala = 1.5;
        this.aumentoEscala = 0.1;

        initColores(15);
        initEtiqueta();
        initVariables();
        initVertices();

        this.hiloCubo = new Thread(this);
        this.hiloCubo.start();
    }

    private void initEtiqueta() {
        JLabel etiquetaActual = new JLabel("Dona #" + (idObjeto + 1));
        this.labelManager.aniadirEtiqueta(etiquetaActual);
    }

    private void initVariables() {
        anguloMaximo = 2 * Math.PI;
        anguloIncremento = anguloMaximo / numPuntos;

        radioMayor = 100;
        radioMenor = 50;
        stepRadioMenor = 0.1;
        decrementando = true;
    }

    private void initVertices() {
        vertices = new ArrayList<>();
        for (double alpha = 0; alpha <= anguloMaximo; alpha += anguloIncremento) {
            for (double beta = 0; beta <= anguloMaximo; beta += anguloIncremento) {
                double[] vertice = new double[3];
                vertice[0] = (radioMayor + radioMenor * Math.cos(alpha)) * Math.cos(beta);
                vertice[1] = (radioMayor + radioMenor * Math.cos(alpha)) * Math.sin(beta);
                vertice[2] = radioMenor * Math.sin(alpha);
                vertices.add(vertice);
            }
        }
        verticesTrasladados = new double[vertices.size()][3];
        transformarVertices();
    }

    private synchronized void dibujarCubo() {
        g2d.resetBuffer();
        initVertices();
        transformarVertices();

        if (mostrarAnimacion) {
            rotaciones[0] += (animacionEjeX) ? 1 : 0;
            rotaciones[1] += (animacionEjeY) ? 1 : 0;
            rotaciones[2] += (animacionEjeZ) ? 1 : 0;
            if (decrementando) {
                radioMenor -= stepRadioMenor;
                if (radioMenor <= 20) {
                    decrementando = false;
                }
            } else {
                radioMenor += stepRadioMenor;
                if (radioMenor >= 50) {
                    decrementando = true;
                }
            }
        }
        if (mostrarPuntos) {
            dibujarPuntos();
        }
        if (mostrarOrigenLuz) {
            mostrarOrigenLuz();
        }
    }

    private void transformarVertices() {
        try {
            for (int i = 0; i < vertices.size(); i++) {
                double[] vertice = vertices.get(i);
                vertice = rotarX(vertice, rotaciones[0]);
                vertice = rotarY(vertice, rotaciones[1]);
                vertice = rotarZ(vertice, rotaciones[2]);
                verticesTrasladados[i] = vertice;
            }

            for (int i = 0; i < vertices.size(); i++) {
                double[] v = verticesTrasladados[i];
                double[] trasladado = {
                    (v[0] * escala) + traslaciones[0] + origenCubo[0],
                    (v[1] * escala) + traslaciones[1] + origenCubo[1],
                    (v[2] * escala) + traslaciones[2] + origenCubo[2]
                };
                verticesTrasladados[i] = trasladado;
            }

            contadorColores = 0;
            for (int i = 0; i < numPuntos; i++) {
                for (int j = 0; j < numPuntos; j++) {
                    int index0 = i * numPuntos + j;
                    int index1 = ((i + 1) % numPuntos) * numPuntos + j;
                    int index2 = i * numPuntos + (j + 1) % numPuntos;
                    int index3 = ((i + 1) % numPuntos) * numPuntos + (j + 1) % numPuntos;

                    double[] vertice0 = verticesTrasladados[index0];
                    double[] vertice1 = verticesTrasladados[index1];
                    double[] vertice2 = verticesTrasladados[index2];
                    double[] vertice3 = verticesTrasladados[index3];

                    Polygon poly = new Polygon();
                    Point2D p0 = punto3D_a_2D(vertice0[0], vertice0[1], vertice0[2]);
                    Point2D p1 = punto3D_a_2D(vertice1[0], vertice1[1], vertice1[2]);
                    Point2D p2 = punto3D_a_2D(vertice2[0], vertice2[1], vertice2[2]);
                    Point2D p3 = punto3D_a_2D(vertice3[0], vertice3[1], vertice3[2]);

                    poly.addPoint((int) p0.getX(), (int) p0.getY());
                    poly.addPoint((int) p1.getX(), (int) p1.getY());
                    poly.addPoint((int) p2.getX(), (int) p2.getY());
                    poly.addPoint((int) p3.getX(), (int) p3.getY());

                    if (mostrarLineas) {
                        g2d.setColor(Color.WHITE);
                        g2d.drawLine((int) p0.getX(), (int) p0.getY(), (int) p1.getX(), (int) p1.getY());
                        g2d.drawLine((int) p1.getX(), (int) p1.getY(), (int) p3.getX(), (int) p3.getY());
                        g2d.drawLine((int) p3.getX(), (int) p3.getY(), (int) p2.getX(), (int) p2.getY());
                        g2d.drawLine((int) p2.getX(), (int) p2.getY(), (int) p0.getX(), (int) p0.getY());
                    }
                    if (mostrarCaras) {
                        if (mostrarLuz) {
                            float[] v0 = arrayDoubleToFloat(vertice0);
                            float[] v1 = arrayDoubleToFloat(vertice1);
                            float[] v2 = arrayDoubleToFloat(vertice2);
                            float[] v3 = arrayDoubleToFloat(vertice3);

                            float[] color = phong.getColorIluminacion(colores[contadorColores % colores.length], brilloEspecular, v0, v1, v2, v3);
                            g2d.setColor(new Color(color[0], color[1], color[2]));
                        } else {
                            g2d.setColor(colores[contadorColores % colores.length]);
                        }
                        double midZ = (vertice0[2] + vertice1[2] + vertice2[2] + vertice3[2]) / 4;
                        g2d.fillPolygon3D(poly, midZ);
                        contadorColores++;
                    }
                }
            }
        } catch (Exception e) {
        }
    }

    private void dibujarPuntos() {
        g2d.setColor(Color.WHITE);
        for (int i = 0; i < verticesTrasladados.length; i++) {
            double[] v = verticesTrasladados[i];
            double x = v[0];
            double y = v[1];
            double z = v[2];
            Point2D.Double p1 = punto3D_a_2D(x, y, z);
            g2d.fillCircle3D((int) p1.x, (int) p1.y, 2, (int) 2);
        }
    }

    @Override
    public void run() {
        int fps = 60;
        int contadorFPS = 0;

        long tiempoPorFotograma = 1000 / fps;
        long sleepTime;

        long tiempoAnteriorFPS = System.currentTimeMillis();
        long tiempoAnteriorLabel = System.currentTimeMillis();

        while (true) {
            if (!isSeleccionado()) {
                try {
                    Thread.sleep(500);
                    g2d.resetBuffer();
                    continue;
                } catch (InterruptedException ex) {
                    Logger.getLogger(Cubo3D.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

            long inicio = System.currentTimeMillis();

            // CODIGO ----------------------------------------------------------
            dibujarCubo();

            Point2D.Double p1 = punto3D_a_2D(traslaciones[0] + origenCubo[0], traslaciones[1] + origenCubo[1], traslaciones[2] + origenCubo[2]);
            labelManager.actualizarEtiquetaObjeto(idObjeto, (int) (p1.x - Constantes.OFFSET_TAG_LABEL_WIDTH), (int) (p1.y - escala - Constantes.OFFSET_TAG_LABEL_HEIGHT));

            // -----------------------------------------------------------------
            long tiempoOperacion = System.currentTimeMillis() - inicio;

            if (numPuntosActual != numPuntos) {
                this.numPuntosActual = numPuntos;
                initVariables();
                initVertices();
            }

            if (System.nanoTime() - tiempoAnteriorLabel >= 250000000) { // 250 ms en nanosegundos
                labelManager.actualizarEtiquetaInformacion(idObjeto, getInformacionObjeto());
                tiempoAnteriorLabel = System.nanoTime();
            }

            if (System.currentTimeMillis() - tiempoAnteriorFPS >= 1000) {
                fpsActuales = contadorFPS;
                contadorFPS = 0;
                tiempoAnteriorFPS = System.currentTimeMillis();
            }
            contadorFPS++;

            sleepTime = Math.abs(tiempoPorFotograma - tiempoOperacion);
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException ex) {
                Logger.getLogger(Cubo3D.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
}
