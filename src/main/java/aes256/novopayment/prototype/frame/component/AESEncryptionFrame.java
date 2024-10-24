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


  public AESEncryptionFrame(IDecrypt iDecrypt, IEncrypt iEncrypt) throws IOException {
    super();
    this.iDecrypt = iDecrypt;
    this.iEncrypt = iEncrypt;
    this.iClientManager = new ClientManager();
    this.iClientManager.cargarClientes();
  }

  public void doOnStart() throws Exception {
    JFrame ventana = new JFrame("AES256 Encryption / Decryption");
    ventana.setSize(480, 400);
    ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    ventana.setLayout(null);
    ventana.setLocationRelativeTo(null); // Center window

    // Estilo inspirado en Ubuntu
    Color backgroundColor = new Color(48, 10, 36); // Dark background
    Color buttonColor = new Color(255, 87, 34);    // Ubuntu orange
    Color textColor = Color.WHITE;
    Font ubuntuFont = new Font("Ubuntu", Font.PLAIN, 14); // Ubuntu font

    ventana.getContentPane().setBackground(backgroundColor);

    JLabel menuLabel = new JLabel("<html>"
            + "|------------------ Menu ------------------|<br>"
            + "| 1. Encrypt text                          <br>"
            + "| 2. Decrypt text                          <br>"
            + "| 3. Add Client                            <br>"
            + "| 4. Add/Update key                        <br>"
            + "| 5. List Clients                          <br>"
            + "| 6. Exit                                  <br>"
            + "|------------------------------------------|</html>");
    menuLabel.setBounds(50, 10, 380, 120);
    menuLabel.setForeground(textColor);
    menuLabel.setFont(ubuntuFont);
    ventana.add(menuLabel);

    // Botones con estilo Ubuntu
    int buttonWidth = 150;
    int buttonHeight = 40;
    JButton btnEncrypt = createUbuntuButton("Encrypt", buttonColor, textColor, ubuntuFont);
    btnEncrypt.setBounds(40, 150, buttonWidth, buttonHeight);

    JButton btnDecrypt = createUbuntuButton("Decrypt", buttonColor, textColor, ubuntuFont);
    btnDecrypt.setBounds(250, 150, buttonWidth, buttonHeight);

    JButton btnAddClient = createUbuntuButton("Add Client", buttonColor, textColor, ubuntuFont);
    btnAddClient.setBounds(40, 210, buttonWidth, buttonHeight);

    JButton btnAddKey = createUbuntuButton("Add/Update Key", buttonColor, textColor, ubuntuFont);
    btnAddKey.setBounds(250, 210, buttonWidth, buttonHeight);

    JButton btnListClients = createUbuntuButton("List Clients", buttonColor, textColor, ubuntuFont);
    btnListClients.setBounds(40, 270, buttonWidth, buttonHeight);

    JButton btnExit = createUbuntuButton("Exit", buttonColor, textColor, ubuntuFont);
    btnExit.setBounds(250, 270, buttonWidth, buttonHeight);

    // Añadir los botones a la ventana
    ventana.add(btnEncrypt);
    ventana.add(btnDecrypt);
    ventana.add(btnAddClient);
    ventana.add(btnAddKey);
    ventana.add(btnListClients);
    ventana.add(btnExit);

    // Eventos para los botones
    btnEncrypt.addActionListener(e -> performOperation(Constants.BUSSINES_ENCRYPT));
    btnDecrypt.addActionListener(e -> performOperation(Constants.BUSSINES_DECRYPT));
    btnAddClient.addActionListener(e -> agregarCliente());
    btnAddKey.addActionListener(e -> agregarOActualizarLlave());
    btnListClients.addActionListener(e -> listarClientes());
    btnExit.addActionListener(e -> {
      JOptionPane.showMessageDialog(ventana, "Exiting...");
      System.exit(0);
    });

    ventana.setVisible(true);
  }

  private JButton createUbuntuButton(String text, Color bgColor, Color textColor, Font font) {
    JButton button = new JButton(text);
    button.setBackground(bgColor);
    button.setForeground(textColor);
    button.setFont(font);
    button.setFocusPainted(false); // Remove focus border
    button.setBorderPainted(false); // Remove button border
    return button;
  }

  private void performOperation(int operation) {
    try {
      doOnBusinessAes256(operation);
    } catch (Exception ex) {
      JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
    }
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
      int seleccionKey = JOptionPane.showConfirmDialog(null, comboBoxKeys, "Selecciona un key", JOptionPane.OK_CANCEL_OPTION);
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
    String cliente;
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
        this.iClientManager.doOnSaveOrUpdateKey(optionalClient.get().getNombre(), nuevaLlave);
        JOptionPane.showMessageDialog(null, "Llave agregada/actualizada exitosamente.");
      } catch (IOException e) {
        JOptionPane.showMessageDialog(null, "Error al agregar/actualizar la llave: " + e.getMessage());
      }
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
