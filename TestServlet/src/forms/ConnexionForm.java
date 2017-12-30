package forms;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.jasypt.util.password.ConfigurablePasswordEncryptor;

import beans.Utilisateur;
import dao.UtilisateurDao;
import dao.UtilisateurDaoImpl;

public final class ConnexionForm {
    private static final String CHAMP_EMAIL  = "email";
    private static final String CHAMP_PASS   = "motdepasse";
    private static final String ALGO_CHIFFREMENT = "SHA-256";

    private String              resultat;
    private Map<String, String> erreurs      = new HashMap<String, String>();
    private UtilisateurDao      utilisateurDao;
   
    public ConnexionForm( UtilisateurDao utilisateurDAO) {
    	this.utilisateurDao=utilisateurDAO;
    }

    public String getResultat() {
        return resultat;
    }

    public Map<String, String> getErreurs() {
        return erreurs;
    }

    public Utilisateur connecterUtilisateur( HttpServletRequest request ) {
        /* Récupération des champs du formulaire */
        String email = getValeurChamp( request, CHAMP_EMAIL );
        String motDePasse = getValeurChamp( request, CHAMP_PASS );
        
        Utilisateur utilisateur = utilisateurDao.trouver(email);
        

        /* Validation du champ email. */
        try {
           if(email.equals(utilisateur.getEmail())) {
        	   utilisateur.setEmail( email );        	   
           }else {
        	   throw new Exception("Adresse mail non valide." ); 		
		}
        } catch ( Exception e ) {
            setErreur( CHAMP_EMAIL, e.getMessage() );
        }

        /* Validation du champ mot de passe. */
        try {
        	  ConfigurablePasswordEncryptor passwordEncryptor = new ConfigurablePasswordEncryptor();
        	  passwordEncryptor.setAlgorithm( ALGO_CHIFFREMENT );
        	  passwordEncryptor.setPlainDigest( false );
        	  
            if(passwordEncryptor.checkPassword(motDePasse, utilisateur.getMotDePasse())) {
            	utilisateur.setMotDePasse( motDePasse );
            }else {            	
            	throw new Exception( "Mot de passe incorrect." );
            }
        } catch ( Exception e ) {
            setErreur( CHAMP_PASS, e.getMessage() );
        }

        /* Initialisation du résultat global de la validation. */
        if ( erreurs.isEmpty() ) {
            resultat = "Succès de la connexion.";
        } else {
            resultat = "Échec de la connexion.";
        }

        return utilisateur;
    }



    /*
     * Ajoute un message correspondant au champ spécifié à la map des erreurs.
     */
    private void setErreur( String champ, String message ) {
        erreurs.put( champ, message );
    }

    /*
     * Méthode utilitaire qui retourne null si un champ est vide, et son contenu
     * sinon.
     */
    private static String getValeurChamp( HttpServletRequest request, String nomChamp ) {
        String valeur = request.getParameter( nomChamp );
        if ( valeur == null || valeur.trim().length() == 0 ) {
            return null;
        } else {
            return valeur;
        }
    }
}