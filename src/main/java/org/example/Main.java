package org.example;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;


public class Main {
    public static void main(String[] args) {

        ArrayList<ArticlesCompra> listaCompra = capturarArticulos();
        generarXML(listaCompra);
        escribirObjetoSerializado(listaCompra);
        deserializar(listaCompra);

        leerXML("lista_compra.xml");

    }

    public static ArrayList<ArticlesCompra> capturarArticulos() {
        ArrayList<ArticlesCompra> listaCompra = new ArrayList<>();
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        try {
            while (true) {
                System.out.println("Ingrese la descripción del artículo (o 'salir' para finalizar):");
                String descripcion = br.readLine();
                if (descripcion.equalsIgnoreCase("salir")) {
                    break;
                }


                System.out.println("Ingrese la cantidad:");
                double cantidad = Double.parseDouble(br.readLine());

                System.out.println("Ingrese la unidad:");
                String unidad = br.readLine();

                System.out.println("Ingrese la sección:");
                String seccion = br.readLine();


                ArticlesCompra articulo = new ArticlesCompra(descripcion, cantidad, unidad, seccion);
                listaCompra.add(articulo);

            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return listaCompra;
    }

    public static void generarXML(ArrayList<ArticlesCompra> listaCompra) {
        try {
            DocumentBuilderFactory docFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docFactory.newDocumentBuilder();

            Document doc = docBuilder.newDocument();
            Element rootElement = doc.createElement("llistacompra");
            doc.appendChild(rootElement);

            for (ArticlesCompra articulo : listaCompra) {
                Element article = doc.createElement("article");
                rootElement.appendChild(article);

                Element descripcion = doc.createElement("descripcio");
                descripcion.appendChild(doc.createTextNode(articulo.getDescripcion()));
                article.appendChild(descripcion);

                Element cantidad = doc.createElement("quantitat");
                cantidad.appendChild(doc.createTextNode(String.valueOf(articulo.getCantidad())));
                article.appendChild(cantidad);

                Element unidad = doc.createElement("unitat");
                unidad.appendChild(doc.createTextNode(articulo.getUnidad()));
                article.appendChild(unidad);

                Element seccion = doc.createElement("seccio");
                seccion.appendChild(doc.createTextNode(articulo.getSeccion()));
                article.appendChild(seccion);


            }

            TransformerFactory transformerFactory = TransformerFactory.newInstance();
            Transformer transformer = transformerFactory.newTransformer();
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");

            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(new File("lista_compra.xml"));

            transformer.transform(source, result);
            System.out.println("Archivo XML generado exitosamente.");
        } catch (ParserConfigurationException | TransformerException e) {
            e.printStackTrace();
        }
    }

    public static void escribirObjetoSerializado(ArrayList<ArticlesCompra> listaCompra) {
        //FileOutputStream sirve para indicarle donde queremos escribir el archivo
        //ObjectOutputStream convierte un objeto en bytes
        //writeObject indica cual es el objeto que queremos guardar
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("lista_compra.txt"))) {
            oos.writeObject(listaCompra);
            oos.close();
            System.out.println("Archivo serializado generado exitosamente.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void deserializar(ArrayList<ArticlesCompra> listaCompra) {


        //preguntar al profe si es mejor crear otro array o usar lista compras

        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream("lista_compra.txt"))) {
            listaCompra = (ArrayList<ArticlesCompra>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

        if (listaCompra != null) {
            try (FileWriter writer = new FileWriter("lista_compra_deserealizada.txt")) {
                for (ArticlesCompra articulo : listaCompra) {
                    String linea = "Descripción: " + articulo.getDescripcion() +
                            ", Cantidad: " + articulo.getCantidad() +
                            ", Unidad: " + articulo.getUnidad() +
                            ", Sección: " + articulo.getSeccion() + "\n";
                    writer.write(linea);
                }
                System.out.println("Datos deserializados y guardados en el archivo lista_compra_deserealizada.txt");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static ArrayList<ArticlesCompra> leerXML(String nombreArchivo) {
        ArrayList<ArticlesCompra> listaCompra = new ArrayList<>();

        try {
            File archivo = new File(nombreArchivo);
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(archivo);
            doc.getDocumentElement().normalize();

            NodeList nodeList = doc.getElementsByTagName("article");

            for (int i = 0; i < nodeList.getLength(); i++) {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE) {
                    Element element = (Element) node;
                    String descripcion = element.getElementsByTagName("descripcio").item(0).getTextContent();
                    double cantidad = Double.parseDouble(element.getElementsByTagName("quantitat").item(0).getTextContent());
                    String unidad = element.getElementsByTagName("unitat").item(0).getTextContent();
                    String seccion = element.getElementsByTagName("seccio").item(0).getTextContent();

                    ArticlesCompra articuloXML = new ArticlesCompra(descripcion, cantidad, unidad, seccion);
                    listaCompra.add(articuloXML);
                }
            }

            System.out.println("Archivo XML leído exitosamente.");
            System.out.println("Número de artículos leídos: " + listaCompra.size());
        } catch (ParserConfigurationException | SAXException | IOException e) {
            e.printStackTrace();
        }

        return listaCompra;
    }




}







