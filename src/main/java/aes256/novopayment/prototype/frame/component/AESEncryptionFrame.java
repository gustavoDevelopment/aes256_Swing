package aes256.novopayment.prototype.frame.component;

import aes256.novopayment.prototype.component.IDecrypt;
import aes256.novopayment.prototype.component.IEncrypt;
import aes256.novopayment.prototype.component.impl.ClientKeyManager;
import aes256.novopayment.prototype.model.Cliente;
import aes256.novopayment.prototype.model.Llave;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class AESEncryptionFrame {

  private IDecrypt iDecrypt;
  private IEncrypt iEncrypt;
  private ClientKeyManager clientKeyManager;


  public AESEncryptionFrame(IDecrypt iDecrypt, IEncrypt iEncrypt) {
    super();
    this.iDecrypt = iDecrypt;
    this.iEncrypt = iEncrypt;
    this.clientKeyManager = new ClientKeyManager();
  }

  public void doOnStart() throws Exception {
    // Crear ventana principal
    JFrame ventana = new JFrame("Menú de Encriptación / Desencriptación AES256");
    ventana.setSize(450, 300);
    ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    ventana.setLayout(null);
    this.clientKeyManager.cargarClientes();

    // Etiqueta para mostrar el menú
    JLabel menuLabel = new JLabel("<html>"
            + "|------------------ Menú ------------------|<br>"
            + "| 1. Encriptar texto                       <br>"
            + "| 2. Desencriptar texto                    <br>"
            + "| 3. Agregar nuevo cliente                  <br>"
            + "| 4. Agregar/Actualizar llave               <br>"
            + "| 5. Listar todos los clientes              <br>"
            + "| 6. Salir                                 <br>"
            + "|------------------------------------------|</html>");
    menuLabel.setBounds(50, 10, 300, 100);
    ventana.add(menuLabel);

    // Definir dimensiones comunes para los botones
    int buttonWidth = 120;
    int buttonHeight = 30;

    // Botones para opciones
    JButton btnEncrypt = new JButton("1");
    btnEncrypt.setBounds(20, 150, buttonWidth, buttonHeight);
    btnEncrypt.setToolTipText("Encriptar texto"); // Título al pasar el mouse
    ventana.add(btnEncrypt);

    JButton btnDecrypt = new JButton("2");
    btnDecrypt.setBounds(150, 150, buttonWidth, buttonHeight);
    btnDecrypt.setToolTipText("Desencriptar texto"); // Título al pasar el mouse
    ventana.add(btnDecrypt);

    JButton btnAddClient = new JButton("3");
    btnAddClient.setBounds(280, 150, buttonWidth, buttonHeight);
    btnAddClient.setToolTipText("Agregar Cliente"); // Título al pasar el mouse
    ventana.add(btnAddClient);

    JButton btnAddKey = new JButton("4");
    btnAddKey.setBounds(20, 200, buttonWidth, buttonHeight);
    btnAddKey.setToolTipText("Agregar/Actualizar Llave"); // Título al pasar el mouse
    ventana.add(btnAddKey);

    JButton btnListClients = new JButton("5");
    btnListClients.setBounds(150, 200, buttonWidth, buttonHeight);
    btnListClients.setToolTipText("Listar Clientes"); // Título al pasar el mouse
    ventana.add(btnListClients);

    JButton btnExit = new JButton("6");
    btnExit.setBounds(280, 200, buttonWidth, buttonHeight); // Centrado
    btnExit.setToolTipText("Salir"); // Título al pasar el mouse
    ventana.add(btnExit);

    // Eventos para los botones
    btnEncrypt.addActionListener(e -> {
      try {
        manejarOperacion("encriptar");
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    });

    btnDecrypt.addActionListener(e -> {
      try {
        manejarOperacion("desencriptar");
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    });

    btnAddClient.addActionListener(e -> agregarCliente());

    btnAddKey.addActionListener(e -> agregarOActualizarLlave());

    btnListClients.addActionListener(e -> listarClientes());

    btnExit.addActionListener(e -> {
      JOptionPane.showMessageDialog(ventana, "Saliendo del programa...");
      System.exit(0);
    });

    ventana.setLocationRelativeTo(null);
    ventana.setVisible(true); // Mostrar la ventana
  }

  // Método para manejar la encriptación o desencriptación
  private void manejarOperacion(String operation) throws Exception {
    String cliente, keyName, data;

    String[] nombresClientes = this.clientKeyManager.clientes.stream()
            .map(Cliente::getNombre) // Obtener los nombres de los clientes
            .toArray(String[]::new); // Convertir a un array

    // Crear el JComboBox para seleccionar un cliente
    JComboBox<String> comboBoxClients = new JComboBox<>(nombresClientes);
    int seleccionClient = JOptionPane.showConfirmDialog(null, comboBoxClients, "Selecciona un cliente", JOptionPane.OK_CANCEL_OPTION);
    if (seleccionClient == JOptionPane.OK_OPTION) {
      cliente = (String) comboBoxClients.getSelectedItem();
      Optional<Cliente> optionalClient = this.clientKeyManager.clientes.stream()
              .filter(c -> c.getNombre().equals(cliente)) // Comparar el nombre
              .findFirst();
      String[] keysNames = optionalClient.get().getLlaves().stream()
              .map(Llave::getName) // Obtener los nombres de los clientes
              .toArray(String[]::new); // Convertir a un array

      // Crear el JComboBox para seleccionar un cliente
      JComboBox<String> comboBoxKeys = new JComboBox<>(keysNames);
      int seleccionKey = JOptionPane.showConfirmDialog(null, comboBoxKeys, "Selecciona un cliente", JOptionPane.OK_CANCEL_OPTION);
      if (seleccionKey == JOptionPane.OK_OPTION) {
        keyName = (String) comboBoxKeys.getSelectedItem();
        Optional<Llave> optionalKey = optionalClient.get().getLlaves().stream()
                .filter(c -> c.getName().equals(keyName)) // Comparar el nombre
                .findFirst();


        data = JOptionPane.showInputDialog("Ingresa el texto:");
        if (operation.equals("encriptar")) {
          String encryptedData = iEncrypt.doOnEncrypt(data, optionalKey.get().getValue());
          JOptionPane.showMessageDialog(null, "Texto Encriptado:\n" + encryptedData);
        } else {
          String decryptedData = iDecrypt.doOnDecrypt(data, optionalKey.get().getValue());
          JOptionPane.showMessageDialog(null, "Texto Desencriptado:\n" + decryptedData);
        }
      } else {
        keyName = "";
      }
    } else {
      cliente = "";
    }


  }

  // Método para agregar un nuevo cliente
  private void agregarCliente() {
    String nombreCliente = JOptionPane.showInputDialog("Ingresa el nombre del cliente:");

    if (nombreCliente == null || nombreCliente.isEmpty()) {
      JOptionPane.showMessageDialog(null, "El nombre del cliente no puede estar vacío.");
      return;
    }

    try {
      // Agregar el nuevo cliente
      Cliente nuevoCliente = new Cliente(nombreCliente, new ArrayList<>());

      JOptionPane.showMessageDialog(null, String.format("Cliente %s", clientKeyManager.agregarCliente(nuevoCliente) ? "agregado  exitosamente" : " ya existe"));
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "Error al agregar el cliente: " + e.getMessage());
    }
  }

  // Método para agregar o actualizar una llave para un cliente
  private void agregarOActualizarLlave() {
    String nombreCliente = JOptionPane.showInputDialog("Ingresa el nombre del cliente:");

    if (nombreCliente == null || nombreCliente.isEmpty()) {
      JOptionPane.showMessageDialog(null, "El nombre del cliente no puede estar vacío.");
      return;
    }

    String nombreLlave = JOptionPane.showInputDialog("Ingresa el nombre de la llave:");

    if (nombreLlave == null || nombreLlave.isEmpty()) {
      JOptionPane.showMessageDialog(null, "El nombre de la llave no puede estar vacío.");
      return;
    }

    String valorLlave = JOptionPane.showInputDialog("Ingresa el valor de la llave:");

    if (valorLlave == null || valorLlave.isEmpty()) {
      JOptionPane.showMessageDialog(null, "El valor de la llave no puede estar vacío.");
      return;
    }

    try {
      // Agregar o actualizar la llave
      Llave nuevaLlave = new Llave(nombreLlave, valorLlave);
      clientKeyManager.agregarOActualizarLlave(nombreCliente, nuevaLlave);
      JOptionPane.showMessageDialog(null, "Llave agregada/actualizada exitosamente.");
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "Error al agregar/actualizar la llave: " + e.getMessage());
    }
  }

  // Método para listar todos los clientes y sus llaves
  private void listarClientes() {
    try {
      List<Cliente> clientes = clientKeyManager.cargarClientes();
      ObjectMapper objectMapper = new ObjectMapper(); // Inicializar el ObjectMapper
      String jsonClientes;

      if (clientes != null && !clientes.isEmpty()) {
        // Convertir la lista de clientes a JSON
        jsonClientes = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(clientes);
      } else {
        jsonClientes = "No hay clientes para mostrar.";
      }

      // Crear un nuevo JFrame para mostrar el JSON
      JFrame frame = new JFrame("Lista de Clientes");
      frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
      frame.setSize(400, 300);
      frame.setLayout(new BorderLayout());

      // Crear un JTextArea para mostrar el JSON
      JTextArea textArea = new JTextArea(jsonClientes);
      textArea.setEditable(false); // Hacer que el área de texto no sea editable
      textArea.setLineWrap(true); // Habilitar el ajuste de línea
      textArea.setWrapStyleWord(true); // Ajustar por palabras

      // Crear un JScrollPane y agregar el JTextArea a él
      JScrollPane scrollPane = new JScrollPane(textArea);
      scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS); // Siempre mostrar la barra de desplazamiento vertical
      scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED); // Mostrar la barra de desplazamiento horizontal solo cuando sea necesario

      frame.add(scrollPane, BorderLayout.CENTER); // Agregar el JScrollPane al JFrame

      frame.setLocationRelativeTo(null); // Centrar el JFrame en la pantalla
      frame.setVisible(true); // Mostrar el JFrame
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "Error al listar los clientes: " + e.getMessage());
    }
  }


}
