package aes256.novopayment.prototype.model;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Builder
@Data // Genera getters, setters, toString, equals, y hashCode
@NoArgsConstructor // Genera un constructor sin argumentos
@AllArgsConstructor // Genera un constructor con todos los argumentos
@EqualsAndHashCode(of = "nombre")
public class Cliente {
    @JsonProperty("nombre")
	private String nombre;
    
    @JsonProperty("llaves")
    private List<Llave> llaves= new ArrayList<Llave>();

}

