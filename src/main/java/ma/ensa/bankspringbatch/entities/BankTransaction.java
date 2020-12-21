package ma.ensa.bankspringbatch.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Transient;
import java.util.Date;

@Entity
@Data @NoArgsConstructor @AllArgsConstructor @ToString
public class BankTransaction {
    @Id
    private Long id;// on ajoute pas l'annotation de génération automatique d'Id car on dans un traitement de données déja existantes.
    private long accountID;
    private Date transactionDate;
    /*
    * Dans les fichiers textes souvant les dates sont écrites dans un format particulier,
    * alors pour eviter le probleme de formatage de la date on définie un autre champs de type String
    * Puis on applique la DateFormat sur ce String.
    * */
    @Transient
    private String strTransactionDate;
    private String transactionType;
    private double amount;
}
