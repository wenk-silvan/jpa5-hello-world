/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package HelloJpa;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.NoResultException;
import javax.persistence.Persistence;
import javax.persistence.Query;
import javax.persistence.TypedQuery;

/**
 *
 * select * from Professoren;
 *
 * select * from Vorlesungen;
 *
 * alter table Vorlesungen drop foreign key vorlesungen_ibfk_1;
 *
 * alter table Vorlesungen add foreign key (gelesenVon) references
 * Professoren(PersNr) on delete cascade;
 *
 * DELETE FROM `uni2`.`Professoren` WHERE Name = 'Precht';
 *
 * @author tqkaufma
 */
public class HelloJPA {

    /**
     */
    public static void main(String[] args) {
    	EntityManager em = Persistence
    			.createEntityManagerFactory("HelloJpaPU2")
    			.createEntityManager();

    	bProfessoren(em);
    	cVorlesungenSokrates1(em);
    	cVorlesungenSokrates2(em);
    	cVorlesungenSokrates3(em, 2125);
    	dProfessorenJoin(em);
    	eUpdateSokrates(em);
    	//AddProfessor(em);
    	//gAddVorlesung(em);
    }
    
    private static void bProfessoren(EntityManager em) {
    	System.out.println("--------Aufgabe B--------");
    	List<?> list = em
    			.createNamedQuery("Professoren.findAll")
    			.getResultList();  
    	
    	list.forEach(l -> {
            Professoren p = (Professoren) l;
            System.out.println(p.getName());
    	});
    	System.out.println();
    }
    
    private static void cVorlesungenSokrates1(EntityManager em) {
    	System.out.println("--------Aufgabe C-1------");
    	List<?> list = em
    			.createNamedQuery("Vorlesungen.findAll")
    			.getResultList();
    	
        System.out.println("Alle Vorlesungen von Sokrates:");  
    	list.forEach(l -> {
            Vorlesungen v = (Vorlesungen) l;
            if (v.getGelesenVon().getName().equals("Sokrates")) {
                System.out.println("- " + v.getTitel());            	
            }
    	});
    	System.out.println();
    }
    
    private static void cVorlesungenSokrates2(EntityManager em) {
    	System.out.println("--------Aufgabe C-2------");
    	Iterator i = em
    			.createQuery("SELECT p.name, v.titel FROM Professoren p INNER JOIN p.vorlesungenCollection v WHERE p.name='Sokrates'")
    			.getResultList()
    			.iterator();
    	
        while(i.hasNext()) {
        Object[] o = (Object[]) i.next();
              System.out.println(o[0] + " liest " + o[1]);
        }
    	System.out.println();
    }
    
    private static void cVorlesungenSokrates3(EntityManager em, int nr) {
    	System.out.println("--------Aufgabe C-3------");
    	String strQuery = "SELECT v FROM Vorlesungen v WHERE v.gelesenVon.persNr = :persNr";
    	
    	TypedQuery<Vorlesungen> tq = em.createQuery(strQuery, Vorlesungen.class);
    	tq.setParameter("persNr", nr);
    	try {
    		List<Vorlesungen> list = tq.getResultList();
    		Iterator<Vorlesungen> i = list.iterator();
    		while(i.hasNext()) {
    			System.out.println(i.next().getTitel());
    		}
    	}
    	catch(NoResultException ex) {
    		ex.printStackTrace();
    	} finally {
        	System.out.println();
    	}
    }

    private static void dProfessorenJoin(EntityManager em) {
    	System.out.println("--------Aufgabe D--------");
        Iterator i = em
        		.createQuery("SELECT p.name, v.titel FROM Professoren p INNER JOIN Vorlesungen v")
        		.getResultList()
        		.iterator();
        
        while(i.hasNext()) {
        	Object[] o = (Object[]) i.next();
        	System.out.println(o[0] + " liest " + o[1]);
        }
    }
    
    private static void eUpdateSokrates(EntityManager em) {
    	System.out.println("--------Aufgabe E--------");
    	Iterator i = em
    			.createQuery("SELECT p FROM Professoren p  WHERE p.name='Sokrates'")
    			.getResultList()
    			.iterator();
    	
    	Professoren sokrates;
    	while(i.hasNext()) {
    	      sokrates = (Professoren) i.next();
    	      System.out.println(sokrates);
    	      EntityTransaction ta = em.getTransaction();
    	      ta.begin();
    	      sokrates.setRaum(1234);
    	      em.merge(sokrates);
    	      ta.commit();
    	      System.out.println(sokrates);
    	}
    	System.out.println();
    }
    
    private static void fAddProfessor(EntityManager em) {
    	System.out.println("--------Aufgabe F--------");
    	Professoren precht = new Professoren(1291, "Precht");
    	System.out.println(precht);
    	EntityTransaction ta = em.getTransaction();
    	ta.begin();
    	em.persist(precht);
    	ta.commit();

    	Iterator i = em
    			.createQuery("SELECT p FROM Professoren p WHERE p.name='Precht'")
    			.getResultList()
    			.iterator();
    	
    	Professoren prechtDb;
    	while(i.hasNext()) {
    		prechtDb = (Professoren) i.next();
    	 	System.out.println(prechtDb);
    	}
    }
    
    private static void gAddVorlesung(EntityManager em) {
    	System.out.println("--------Aufgabe G--------");
    	Iterator i = em
    			.createQuery("SELECT p FROM Professoren p WHERE p.name='Precht'")
    			.getResultList()
    			.iterator();
    			
    	while(i.hasNext()) {
    		 Professoren profPrecht = (Professoren) i.next();
    	 	 System.out.println(profPrecht);
    	 	 List<Vorlesungen> vorlesungen = new ArrayList<>();
    	     Vorlesungen postmoderne = new Vorlesungen(1267);
    	     postmoderne.setTitel("Postmoderne");
    	     postmoderne.setGelesenVon(profPrecht);
    	     postmoderne.setSws(4);
    	     vorlesungen.add(postmoderne);
    	     EntityTransaction trans = em.getTransaction();
    	     trans.begin();
    	     profPrecht.setVorlesungenCollection(vorlesungen);
    	     em.merge(profPrecht);
    	     trans.commit();
    	}
    }
}
