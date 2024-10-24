package aes256.novopayment.prototype.component;

import aes256.novopayment.prototype.model.Cliente;
import aes256.novopayment.prototype.model.Llave;

import java.io.IOException;
import java.util.List;

public interface IClientManager {
  // Método para agregar un nuevo cliente
  boolean doOnAddCliente(Cliente nuevoCliente) throws IOException;

  // Método para agregar o actualizar una llave para un cliente
  void doOnSaveOrUpdateKey(String nombreCliente, Llave nuevaLlave) throws IOException;


  // Método para cargar clientes
  List<Cliente> cargarClientes() throws IOException;

  List<Cliente> doOnGetClients();
}
