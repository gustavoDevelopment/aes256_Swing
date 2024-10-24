package aes256.novopayment.prototype.frame.component;

import aes256.novopayment.prototype.component.IClientManager;
import aes256.novopayment.prototype.component.IDecrypt;
import aes256.novopayment.prototype.component.IEncrypt;
import aes256.novopayment.prototype.component.impl.ClientManager;
import aes256.novopayment.prototype.constants.Constants;
import aes256.novopayment.prototype.model.Cliente;
import aes256.novopayment.prototype.model.Llave;
import com.fasterxml.jackson.databind.ObjectMapper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


public class AESEncryptionFrame {

  private IDecrypt iDecrypt;
  private IEncrypt iEncrypt;
  private IClientManager iClientManager;


  public AESEncryptionFrame(IDecrypt iDecrypt, IEncrypt iEncrypt) {
    super();
    this.iDecrypt = iDecrypt;
    this.iEncrypt = iEncrypt;
    this.iClientManager = new ClientManager();
  }

  public void doOnStart() throws Exception {
    JFrame ventana = new JFrame("Menú de Encriptación / Desencriptación AES256");
    ventana.setSize(450, 300);
    ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    ventana.setLayout(null);
    this.iClientManager.cargarClientes();
    JLabel menuLabel = new JLabel("<html>"
            + "|------------------ Menu ------------------|<br>"
            + "| 1. Encrypt text                          <br>"
            + "| 2. Decrypt text                          <br>"
            + "| 3. Add Client                            <br>"
            + "| 4. Add/Update key                        <br>"
            + "| 5. List Clients                          <br>"
            + "| 6. Exit                                  <br>"
            + "|------------------------------------------|</html>");

    menuLabel.setBounds(50, 10, 300, 100);
    ventana.add(menuLabel);
    int buttonWidth = 120;
    int buttonHeight = 30;

    // Botones para opciones
    JButton btnEncrypt = new JButton("Encrypt");
    btnEncrypt.setBounds(20, 150, buttonWidth, buttonHeight);
    btnEncrypt.setToolTipText("Encrypt"); // Título al pasar el mouse
    ventana.add(btnEncrypt);

    JButton btnDecrypt = new JButton("Decrypt");
    btnDecrypt.setBounds(150, 150, buttonWidth, buttonHeight);
    btnDecrypt.setToolTipText("Decrypt"); // Título al pasar el mouse
    ventana.add(btnDecrypt);

    JButton btnAddClient = new JButton("Add Client");
    btnAddClient.setBounds(280, 150, buttonWidth, buttonHeight);
    btnAddClient.setToolTipText("Add Client"); // Título al pasar el mouse
    ventana.add(btnAddClient);

    JButton btnAddKey = new JButton("Add/Update key");
    btnAddKey.setBounds(20, 200, buttonWidth, buttonHeight);
    btnAddKey.setToolTipText("Add/Update key"); // Título al pasar el mouse
    ventana.add(btnAddKey);

    JButton btnListClients = new JButton("List Clients");
    btnListClients.setBounds(150, 200, buttonWidth, buttonHeight);
    btnListClients.setToolTipText("List Clients"); // Título al pasar el mouse
    ventana.add(btnListClients);

    JButton btnExit = new JButton("Exit");
    btnExit.setBounds(280, 200, buttonWidth, buttonHeight); // Centrado
    btnExit.setToolTipText("EXIT"); // Título al pasar el mouse
    ventana.add(btnExit);

    // Eventos para los botones
    btnEncrypt.addActionListener(e -> {
      try {
        doOnBusinessAes256(Constants.BUSSINES_ENCRYPT);
      } catch (Exception ex) {
        throw new RuntimeException(ex);
      }
    });

    btnDecrypt.addActionListener(e -> {
      try {
        doOnBusinessAes256(Constants.BUSSINES_DECRYPT);
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
  private void doOnBusinessAes256(int operation) throws Exception {
    String cliente, keyName, data;

    String[] nombresClientes = this.iClientManager.doOnGetClients().stream()
            .map(Cliente::getNombre) // Obtener los nombres de los clientes
            .toArray(String[]::new); // Convertir a un array

    // Crear el JComboBox para seleccionar un cliente
    JComboBox<String> comboBoxClients = new JComboBox<>(nombresClientes);
    int seleccionClient = JOptionPane.showConfirmDialog(null, comboBoxClients, "Selecciona un cliente", JOptionPane.OK_CANCEL_OPTION);
    if (seleccionClient == JOptionPane.OK_OPTION) {
      cliente = (String) comboBoxClients.getSelectedItem();
      Optional<Cliente> optionalClient = this.iClientManager.doOnGetClients().stream()
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
        if (operation == Constants.BUSSINES_ENCRYPT) {
          String encryptedData = iEncrypt.doOnEncrypt(data, optionalKey.get().getValue());
          JOptionPane.showMessageDialog(null, "Texto Encriptado:\n" + encryptedData);
        } else if (operation == Constants.BUSSINES_DECRYPT) {
          String decryptedData = iDecrypt.doOnDecrypt(data, optionalKey.get().getValue());
          JOptionPane.showMessageDialog(null, "Texto Desencriptado:\n" + decryptedData);
        }
      }
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

      JOptionPane.showMessageDialog(null, String.format("Cliente %s", this.iClientManager.doOnAddCliente(nuevoCliente) ? "agregado  exitosamente" : " ya existe"));
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
      this.iClientManager.doOnSaveOrUpdateKey(nombreCliente, nuevaLlave);
      JOptionPane.showMessageDialog(null, "Llave agregada/actualizada exitosamente.");
    } catch (IOException e) {
      JOptionPane.showMessageDialog(null, "Error al agregar/actualizar la llave: " + e.getMessage());
    }
  }

  // Método para listar todos los clientes y sus llaves
  private void listarClientes() {
    try {
      List<Cliente> clientes = this.iClientManager.cargarClientes();
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
