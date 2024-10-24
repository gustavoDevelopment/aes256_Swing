package aes256.novopayment.prototype.component.impl;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import aes256.novopayment.prototype.component.IClientManager;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import aes256.novopayment.prototype.model.Cliente;
import aes256.novopayment.prototype.model.Llave;

public class ClientManager  implements IClientManager{

  private static final String FILE_PATH = "client_keys.json";
  private ObjectMapper objectMapper = new ObjectMapper();

  public List<Cliente> clientes = new ArrayList<>();

  public ClientManager() {
  }

  // Método para agregar un nuevo cliente
  public boolean doOnAddCliente(Cliente nuevoCliente) throws IOException {
    if (!this.clientes.contains(nuevoCliente)) {
      this.clientes.add(nuevoCliente);
      doOnWriteFile(clientes);
      return true;
    }
    return false;
  }

  @Override
  // Método para agregar o actualizar una llave para un cliente
  public void doOnSaveOrUpdateKey(String nombreCliente, Llave nuevaLlave) throws IOException {
    List<Cliente> clientesExistentes = cargarClientes();

    // Buscar cliente
    Optional<Cliente> clienteOpt = clientesExistentes.stream()
            .filter(cliente -> cliente.getNombre().equals(nombreCliente)).findFirst();

    if (clienteOpt.isPresent()) {
      Cliente cliente = clienteOpt.get();
      List<Llave> llaves = cliente.getLlaves();

      // Verificar si la llave ya existe y actualizarla, si no, agregar la nueva
      Optional<Llave> llaveExistenteOpt = llaves.stream()
              .filter(llave -> llave.getName().equals(nuevaLlave.getName())).findFirst();

      if (llaveExistenteOpt.isPresent()) {
        llaveExistenteOpt.get().setValue(nuevaLlave.getValue());
      } else {
        llaves.add(nuevaLlave);
      }

      // Guardar los cambios
      doOnWriteFile(clientesExistentes);
    } else {
      System.out.println("Cliente no encontrado.");
    }
  }

  // Método para guardar clientes
  private void doOnWriteFile(List<Cliente> clientes) throws IOException {
    objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(FILE_PATH), clientes);
  }

  @Override
  // Método para cargar clientes
  public List<Cliente> cargarClientes() throws IOException {
    File file = new File(FILE_PATH);
    if (!file.exists()) {
      return new ArrayList<>(); // Retornar una lista vacía si el archivo no existe
    }

    // Verificar si el archivo está vacío
    if (file.length() == 0) {
      System.out.println("El archivo está vacío. Retornando una lista vacía.");
      return new ArrayList<>(); // Retornar una lista vacía si el archivo está vacío
    }

    this.clientes.clear();
    // Leer el archivo JSON y convertirlo a la lista de clientes
    this.clientes.addAll(objectMapper.readValue(file, new TypeReference<List<Cliente>>() {
    }));

    return clientes;
  }

  @Override
  public List<Cliente> doOnGetClients(){
    return this.clientes;
  }
}
