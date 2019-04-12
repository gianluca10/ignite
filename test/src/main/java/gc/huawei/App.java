package gc.huawei;
import org.apache.ignite.Ignite;
import org.apache.ignite.IgniteCache;
import org.apache.ignite.Ignition;
import org.apache.ignite.configuration.IgniteConfiguration;
import org.apache.ignite.spi.discovery.tcp.TcpDiscoverySpi;
import org.apache.ignite.spi.discovery.tcp.ipfinder.multicast.TcpDiscoveryMulticastIpFinder;
import org.apache.ignite.configuration.CacheConfiguration;
import org.apache.ignite.cache.CacheMode;
import gc.dto.*;
import org.apache.ignite.cache.query.SqlQuery;
import org.apache.ignite.cache.query.SqlFieldsQuery;
import org.apache.ignite.cache.query.*;
import java.util.*;
import java.lang.Iterable;

public class App 
{
    private static final String PERSON_CACHE_NAME = App.class.getSimpleName() + "-persons";
    private static final String PRODUCT_CACHE_NAME = App.class.getSimpleName() + "-products";
    private static final String ORDER_CACHE_NAME = App.class.getSimpleName() + "-orders";
    private static long order_count = 0l;


    public static void main( String[] args )
    {
	    // create a new instance of TCP Discovery SPI
	    TcpDiscoverySpi spi = new TcpDiscoverySpi();
	    IgniteConfiguration cfg = new IgniteConfiguration();
	    cfg.setClientMode(true);
	    cfg.setDiscoverySpi(spi);


        //CacheConfiguration cachePersonCfg = new CacheConfiguration("Person");
        CacheConfiguration cachePersonCfg = new CacheConfiguration(PERSON_CACHE_NAME);
		cachePersonCfg.setCacheMode(CacheMode.PARTITIONED);
        cachePersonCfg.setBackups(1);
        cachePersonCfg.setIndexedTypes(Integer.class, Person.class);
		cfg.setCacheConfiguration(cachePersonCfg);

        CacheConfiguration cacheProductCfg = new CacheConfiguration(PRODUCT_CACHE_NAME);
		cacheProductCfg.setCacheMode(CacheMode.PARTITIONED);
        cacheProductCfg.setBackups(1);
        cacheProductCfg.setIndexedTypes(Integer.class, Product.class);
		cfg.setCacheConfiguration(cacheProductCfg);

        CacheConfiguration cacheOrderCfg = new CacheConfiguration(ORDER_CACHE_NAME);
		cacheOrderCfg.setCacheMode(CacheMode.PARTITIONED);
        cacheOrderCfg.setBackups(1);
        cacheOrderCfg.setIndexedTypes(Integer.class, Order.class);
		cfg.setCacheConfiguration(cacheOrderCfg);

	    Ignite ignite = Ignition.start(cfg);
	    //IgniteCache < Integer, String > personCache = ignite.getOrCreateCache("Person"); 
	    // get or create cache
        IgniteCache<Integer, Order> orderCache = ignite.getOrCreateCache(cacheOrderCfg);
        IgniteCache<Integer, Person> personCache = ignite.getOrCreateCache(cachePersonCfg);
        IgniteCache<Integer, Product> productCache = ignite.getOrCreateCache(cacheProductCfg);
        initialize();
        //getPersonNameFromId();
        //getProductNameFromId(21);
        //getProductCountFromId(21);
        // Person p5 = new Person(5, "Rebecca Sanna", 64, a5);
        //   Person p6 = new Person(6, "Marta Guerra", 95, a6);
        //   Person p7 = new Person(7, "Teresa Marino", 45, a7);
        //   Person p8 = new Person(8, "Marzia Lombardi", 54, a8);
        //   Person p9 = new Person(9, "Marika D’angelo", 55, a9);
        //   Product pr5 = new Product(5, "Gold", 112.0f, 100, 0);
        //   Product pr6 = new Product(6, "Silver", 11.0f, 100, 0);
        //   Product pr7 = new Product(7, "Oil", 52.0f, 100, 0);
        //   Product pr8 = new Product(8, "Jam", 3.0f, 100, 0);
        //   Product pr9 = new Product(9, "Cheery", 8.0f, 100, 0);
        //   Product pr10 = new Product(10, "Wood", 1.0f, 100, 0);
        // TESTING 2 POSITIVE CASES
//        placeOrder("Rebecca Sanna", "Oil", 5);
//        placeOrder("Marta Guerra", "Gold", 33);
//        // TESTING NON EXISTING PRODUCT
//        placeOrder("Marzia Lombardi", "Jamail", 21);
//        // TESTING NON EXISTING PERSON
//        placeOrder("Marzio Lombardi", "Jam", 1);
//        // TESTING NOT SUFFICIENT QUANTITY 
//        placeOrder("Marika D’angelo", "Gold", 77);
        IgniteCache< Long, Order> test = Ignition.ignite().cache(ORDER_CACHE_NAME);
        Order order = new Order(1l,1l,1l, 200);
        test.put(1l, order);    
        //print_order_storage_view();
        ignite.close();
    }

    private static boolean placeOrder(String name, String product, int quantity){
        IgniteCache< Long, Order> cache = Ignition.ignite().cache(ORDER_CACHE_NAME);
        Long personId = getPersonIdFromName(name);
        Long productId = getProductIdFromName(product);
        if (personId < 1){
            System.out.println("CUSTOMER NAME NOT FOUND: " + name);
            return false;
        }
        if (productId < 1){
            System.out.println("PRODUCT NAME NOT FOUND: " + product);
            return false;
        }
        int available_quantity = getProductQuantityFromId(productId);
        if (available_quantity < quantity) {
           System.out.println("NOT ENOUGH " + product + " IN STORAGE");
          return false; 
        }
        order_count += 1l;
        Order order = new Order(order_count, personId, productId, quantity);
        cache.put(order_count, order);
        return true;
    }
    
    
/*    private static int getProductIdFromName(String name) {
        IgniteCache< Integer, Product> cache = Ignition.ignite().cache(PRODUCT_CACHE_NAME);
		SqlFieldsQuery sql = new SqlFieldsQuery(
  			"select id from Product where name = '" + name + "'");
		try (QueryCursor<List<?>> cursor = cache.query(sql)) {
  			for (List<?> row : cursor)
    			return row.get(0).getValue().toInteger();
	    }	
        return -1;
	}
*/
    private static Long getPersonIdFromName(String name) {
        String result;
        IgniteCache< Long, Person> cache = Ignition.ignite().cache(PERSON_CACHE_NAME);
    	SqlFieldsQuery sql = new SqlFieldsQuery(
  		"select id from Person where name = '" + name + "'");
        try (QueryCursor<List<?>> cursor = cache.query(sql)) {
        for (List<?> row : cursor){
            result = "" + row.get(0);
            return Long.parseLong(result);
        }
    }
  return -1l;
}
    private static Long getProductIdFromName(String product) {
        String result;
        IgniteCache< Long, Product> cache = Ignition.ignite().cache(PRODUCT_CACHE_NAME);
    	SqlFieldsQuery sql = new SqlFieldsQuery(
  		"select id from Product where name = '" + product + "'");
        try (QueryCursor<List<?>> cursor = cache.query(sql)) {
        for (List<?> row : cursor){
            result = "" + row.get(0);
            return Long.parseLong(result);
        }
    }
  return -1l;
}
    private static int getProductQuantityFromId(long productId){
        String result;
        IgniteCache< Long, Product> cache = Ignition.ignite().cache(PRODUCT_CACHE_NAME);
    	SqlFieldsQuery sql = new SqlFieldsQuery(
  		"select quantity_in_stock from Product where id = " + productId);
        try (QueryCursor<List<?>> cursor = cache.query(sql)) {
        for (List<?> row : cursor){
            result = "" + row.get(0);
            return Integer.parseInt(result);
        }
    }
       return -1; 
    }
    
    private static void initialize() {

        IgniteCache< Integer, Person> personCache = Ignition.ignite().cache(PERSON_CACHE_NAME);
        IgniteCache< Integer, Product> productCache = Ignition.ignite().cache(PRODUCT_CACHE_NAME);
        IgniteCache< Integer, Order> orderCache = Ignition.ignite().cache(ORDER_CACHE_NAME);
        // Clear caches before start.
        orderCache.clear();
        personCache.clear();
        productCache.clear();

        // People
        Address a1 = new Address("Via Locri", 183);
        Person p1 = new Person(1, "Lisa Gentile", 85, a1);
        Address a2 = new Address("Via Rattazzi", 184);
        Person p2 = new Person(2, "Miriam Parisi", 80, a2);
        Address a3 = new Address("Via Napoli", 184);
        Person p3 = new Person(3, "Gloria Marchetti", 83, a3);
        Address a4 = new Address("Via Urbana", 184);
        Person p4 = new Person(4, "Samantha Mancini", 85, a4);
        Address a5 = new Address("Via Tasso", 185);
        Person p5 = new Person(5, "Rebecca Sanna", 64, a5);
        Address a6 = new Address("Via Fidenza", 182);
        Person p6 = new Person(6, "Marta Guerra", 95, a6);
        Address a7 = new Address("Viale di Trastevere", 153);
        Person p7 = new Person(7, "Teresa Marino", 45, a7);
        Address a8 = new Address("Via Frattina", 187);
        Person p8 = new Person(8, "Marzia Lombardi", 54, a8);
        Address a9 = new Address("Via Gaetano Casati", 154);
        Person p9 = new Person(9, "Marika D’angelo", 55, a9);
        Address a10 = new Address("Via dei Santi Quattro", 184);
        Person p10 = new Person(10, "Giusy Morelli", 18, a10);
        Address a11 = new Address("Piazza di Montecitorio", 186);
        Person p11 = new Person(11, "Matilde Bertelli", 61, a11);
        Address a12 = new Address("Via Giuseppe Parini", 152);
        Person p12 = new Person(12, "Alessandra Venturi", 96, a12);
        Address a13 = new Address("Via Acqui", 183);
        Person p13 = new Person(13, "Rosa Allegra", 37, a13);
        Address a14 = new Address("Largo del Nazareno", 187);
        Person p14 = new Person(14, "Valeria Sartori", 67, a14);
        Address a15 = new Address("Via della Consulta", 184);
        Person p15 = new Person(15, "Valeria Sala", 83, a15);
        Address a16 = new Address("Via Acciaioli", 186);
        Person p16 = new Person(16, "Barbara Galilei", 57, a16);
        Address a17 = new Address("Via del Governo Vecchio", 186);
        Person p17 = new Person(17, "Valentina Fabbri", 19, a17);
        Address a18 = new Address("Via Faleria", 183);
        Person p18 = new Person(18, "Federica Giordano", 26, a18);
        Address a19 = new Address("Via Bartolomeo Cristofori", 146);
        Person p19 = new Person(19, "Antonella Berlusconi", 53, a19);
        Address a20 = new Address("Via della Dogana Vecchia", 186);
        Person p20 = new Person(20, "Viola Marino", 75, a20);
        Address a21 = new Address("Via Giovanni Villani", 179);
        Person p21 = new Person(21, "Lisa Nicoli", 89, a21);
        Address a22 = new Address("Via Del Paradiso", 186);
        Person p22 = new Person(22, "Giada Scotti", 42, a22);
        Address a23 = new Address("Via Crema", 182);
        Person p23 = new Person(23, "Nadia Marchetti", 33, a23);
        Address a24 = new Address("Via Pozzuoli", 182);
        Person p24 = new Person(24, "Matilde Barbieri", 27, a24);
        Address a25 = new Address("Via Borghesano Lucchese", 146);
        Person p25 = new Person(25, "Lucia Favero", 60, a25);
        Address a26 = new Address("Lungotevere Marzio", 186);
        Person p26 = new Person(26, "Stefania Mancini", 69, a26);
        Address a27 = new Address("Via dei Chiavari", 186);
        Person p27 = new Person(27, "Samantha Romano", 25, a27);
        Address a28 = new Address("Piazza di Porta Maggiore", 185);
        Person p28 = new Person(28, "Cristina Leone", 62, a28);
        Address a29 = new Address("Via di Sant'Anna", 186);
        Person p29 = new Person(29, "Sonia Silvestri", 83, a29);
        Address a30 = new Address("Via Francesco Passino", 154);
        Person p30 = new Person(30, "Lucrezia Guerra", 44, a30);
        Address a31 = new Address("Via Ardea", 183);
        Person p31 = new Person(31, "Matilde Carbone", 42, a31);
        Address a32 = new Address("Circonvallazione Gianicolense", 152);
        Person p32 = new Person(32, "Roberta Greco", 68, a32);
        Address a33 = new Address("Via dei Cappellari", 186);
        Person p33 = new Person(33, "Alice Ferrari", 68, a33);
        Address a34 = new Address("Piazza Bartolomeo Romano", 154);
        Person p34 = new Person(34, "Gioia Sanna", 64, a34);
        Address a35 = new Address("Viale dello Scalo San Lorenzo", 185);
        Person p35 = new Person(35, "Emma Allegra", 55, a35);
        Address a36 = new Address("Piazza della Città Leonina", 193);
        Person p36 = new Person(36, "Angelica Raffa", 32, a36);
        Address a37 = new Address("Via della Minerva", 186);
        Person p37 = new Person(37, "Elisa Marino", 36, a37);
        Address a38 = new Address("Via Urbana", 184);
        Person p38 = new Person(38, "Simona Caruso", 46, a38);
        Address a39 = new Address("Lungotevere Marzio", 186);
        Person p39 = new Person(39, "Ludovica Pellegrini", 87, a39);
        Address a40 = new Address("Via Portuense", 149);
        Person p40 = new Person(40, "Sonia Morelli", 73, a40);
        Address a41 = new Address("Via Cesare De Lollis", 185);
        Person p41 = new Person(41, "Noemi Moretti", 85, a41);
        Address a42 = new Address("Via Eugenio Barsanti", 146);
        Person p42 = new Person(42, "Ginevra Carozza", 84, a42);
        Address a43 = new Address("Piazza Vittorio Bottego", 154);
        Person p43 = new Person(43, "Serena Borghese", 33, a43);
        Address a44 = new Address("Via Cavour", 185);
        Person p44 = new Person(44, "Mary Barbieri", 54, a44);
        Address a45 = new Address("Via dei Marsi", 185);
        Person p45 = new Person(45, "Cristina Agosti", 66, a45);
        Address a46 = new Address("Via Massimo D'Azeglio", 184);
        Person p46 = new Person(46, "Alessia Fabbri", 66, a46);
        Address a47 = new Address("Via di Campo Marzio", 186);
        Person p47 = new Person(47, "Linda Venturi", 96, a47);
        Address a48 = new Address("Via Caltagirone", 182);
        Person p48 = new Person(48, "Caterina Bianco", 51, a48);
        Address a49 = new Address("Via Aventina", 153);
        Person p49 = new Person(49, "Marta Longo", 46, a49);
        Address a50 = new Address("Via Barberini", 187);
        Person p50 = new Person(50, "Vanessa Zunino", 39, a50);
        Address a51 = new Address("Vicolo del Piombo", 187);
        Person p51 = new Person(51, "Alice Monti", 78, a51);
        Address a52 = new Address("Piazza di San Lorenzo in Lucina", 186);
        Person p52 = new Person(52, "Antonella Coppola", 73, a52);
        Address a53 = new Address("Via Torino", 184);
        Person p53 = new Person(53, "Daniela Uberti", 39, a53);
        Address a54 = new Address("Via Amiterno", 183);
        Person p54 = new Person(54, "Silvia Barilla", 90, a54);
        Address a55 = new Address("Via San Martino ai Monti", 184);
        Person p55 = new Person(55, "Samantha Fabbri", 29, a55);
        Address a56 = new Address("Piazza Venezia", 186);
        Person p56 = new Person(56, "Camilla Accardi", 38, a56);
        Address a57 = new Address("Piazzale Enrico Dunant", 152);
        Person p57 = new Person(57, "Giada Puddu", 98, a57);
        Address a58 = new Address("Via Rosa Raimondi Garibaldi", 145);
        Person p58 = new Person(58, "Linda Allegra", 26, a58);
        Address a59 = new Address("Piazza della Pilotta", 187);
        Person p59 = new Person(59, "Federica Santoro", 79, a59);
        Address a60 = new Address("Via Anton Giulio Barrili", 152);
        Person p60 = new Person(60, "Julia Barbieri", 53, a60);
        Address a61 = new Address("Via Guglielmo Ciamarra", 154);
        Person p61 = new Person(61, "Silvia Greco", 50, a61);
        Address a62 = new Address("Via del Governo Vecchio", 186);
        Person p62 = new Person(62, "Camilla Barbieri", 92, a62);
        Address a63 = new Address("Via Varese", 185);
        Person p63 = new Person(63, "Giusy Conte", 97, a63);
        Address a64 = new Address("Via del Monte della Farina", 186);
        Person p64 = new Person(64, "Giada Marino", 51, a64);
        Address a65 = new Address("Via Gioberti", 185);
        Person p65 = new Person(65, "Emma Farina", 86, a65);
        Address a66 = new Address("Via di Parione", 186);
        Person p66 = new Person(66, "Rosa Vitale", 54, a66);
        Address a67 = new Address("Via Anton Giulio Barrili", 152);
        Person p67 = new Person(67, "Debora De santis", 91, a67);
        Address a68 = new Address("Via Enrico Fermi", 146);
        Person p68 = new Person(68, "Simona Ferrante", 84, a68);
        Address a69 = new Address("Via Trebula", 183);
        Person p69 = new Person(69, "Emma Bruni", 82, a69);
        Address a70 = new Address("Via della Reginella", 186);
        Person p70 = new Person(70, "Erica Puma", 100, a70);
        Address a71 = new Address("Piazza Navona", 186);
        Person p71 = new Person(71, "Ilenia Grasso", 91, a71);
        Address a72 = new Address("Via Beniamino Franklin", 153);
        Person p72 = new Person(72, "Serena Bernardi", 42, a72);
        Address a73 = new Address("Via Alessandro Volta", 153);
        Person p73 = new Person(73, "Benedetta Marchetti", 45, a73);
        Address a74 = new Address("Via Terni", 182);
        Person p74 = new Person(74, "Stefania Guerra", 30, a74);
        Address a75 = new Address("Via Antonio Rosmini", 184);
        Person p75 = new Person(75, "Marika Rossi", 40, a75);
        Address a76 = new Address("Largo Angelicum", 184);
        Person p76 = new Person(76, "Mary Rossi", 34, a76);
        Address a77 = new Address("Piazza dell'Emporio", 153);
        Person p77 = new Person(77, "Monica Lamberti", 65, a77);
        Address a78 = new Address("Piazza Mattei", 186);
        Person p78 = new Person(78, "Gabriella Monti", 23, a78);
        Address a79 = new Address("Via Licinia", 153);
        Person p79 = new Person(79, "Stefania Bianchi", 76, a79);
        Address a80 = new Address("Via Modena", 184);
        Person p80 = new Person(80, "Teresa Santoro", 34, a80);
        Address a81 = new Address("Via Germano Sommeiller", 185);
        Person p81 = new Person(81, "Linda Orsini", 66, a81);
        Address a82 = new Address("Corso Vittorio Emanuele II", 186);
        Person p82 = new Person(82, "Ludovica Greco", 80, a82);
        Address a83 = new Address("Via Benedetto Varchi", 179);
        Person p83 = new Person(83, "Luisa Lamberti", 89, a83);
        Address a84 = new Address("Via Costanza Baudana Vaccolini", 153);
        Person p84 = new Person(84, "Mary Valentino", 61, a84);
        Address a85 = new Address("Via Guglielmo Massaia", 154);
        Person p85 = new Person(85, "Sofia Bertelli", 44, a85);
        Address a86 = new Address("Via Noto", 182);
        Person p86 = new Person(86, "Gioia Mariano", 76, a86);
        Address a87 = new Address("Via Tito Omboni", 147);
        Person p87 = new Person(87, "Lisa Di fazio", 70, a87);
        Address a88 = new Address("Via degli Avignonesi", 187);
        Person p88 = new Person(88, "Daniela Favero", 55, a88);
        Address a89 = new Address("Via dei Serpenti", 184);
        Person p89 = new Person(89, "Caterina Carbone", 40, a89);
        Address a90 = new Address("Piazza Albania", 153);
        Person p90 = new Person(90, "Jessica Brambilla", 74, a90);
        Address a91 = new Address("Via Anton Giulio Barrili", 152);
        Person p91 = new Person(91, "Emma Accardi", 26, a91);
        Address a92 = new Address("Via di Donna Olimpia", 152);
        Person p92 = new Person(92, "Sarah Valentino", 42, a92);
        Address a93 = new Address("Via della Dogana Vecchia", 186);
        Person p93 = new Person(93, "Silvia Ranallo", 70, a93);
        Address a94 = new Address("Piazza San Giovanni in Laterano", 184);
        Person p94 = new Person(94, "Matilde Bellucci", 20, a94);
        Address a95 = new Address("Via Monte Polacco", 184);
        Person p95 = new Person(95, "Annalisa Romano", 24, a95);
        Address a96 = new Address("Via Faleria", 183);
        Person p96 = new Person(96, "Alessandra Gatti", 70, a96);
        Address a97 = new Address("Vicolo della Scala", 153);
        Person p97 = new Person(97, "Marika Fontana", 37, a97);
        Address a98 = new Address("Via dei Fienaroli", 153);
        Person p98 = new Person(98, "Ilaria De santis", 61, a98);
        Address a99 = new Address("Via Merulana", 184);
        Person p99 = new Person(99, "Nicole Verga", 81, a99);
        Address a100 = new Address("Via Giulia", 186);
        Person p100 = new Person(100, "Marika Allegra", 31, a100);
       /* Person p1 = new Person(1, "John", "Doe", 20);
        Person p2 = new Person(2, "Jane", "Doe", 10);
        Person p3 = new Person(3, "John", "Smith", 10);
        Person p4 = new Person(4, "Jane", "Smith", 20);
        Person p5 = new Person(5, "Helen", "Gerrard", 80);
        Person p6 = new Person(6, "Darth", "Vader", 12); 
        personCache.put(1, p1);
        personCache.put(2, p2);
        personCache.put(3, p3);
        personCache.put(4, p4);
        personCache.put(5, p5);
		personCache.put(6, p6);
        */
        personCache.put(1, p1);
        personCache.put(2, p2);
        personCache.put(3, p3);
        personCache.put(4, p4);
        personCache.put(5, p5);
        personCache.put(6, p6);
        personCache.put(7, p7);
        personCache.put(8, p8);
        personCache.put(9, p9);
        personCache.put(10, p10);
        personCache.put(11, p11);
        personCache.put(12, p12);
        personCache.put(13, p13);
        personCache.put(14, p14);
        personCache.put(15, p15);
        personCache.put(16, p16);
        personCache.put(17, p17);
        personCache.put(18, p18);
        personCache.put(19, p19);
        personCache.put(20, p20);
        personCache.put(21, p21);
        personCache.put(22, p22);
        personCache.put(23, p23);
        personCache.put(24, p24);
        personCache.put(25, p25);
        personCache.put(26, p26);
        personCache.put(27, p27);
        personCache.put(28, p28);
        personCache.put(29, p29);
        personCache.put(30, p30);
        personCache.put(31, p31);
        personCache.put(32, p32);
        personCache.put(33, p33);
        personCache.put(34, p34);
        personCache.put(35, p35);
        personCache.put(36, p36);
        personCache.put(37, p37);
        personCache.put(38, p38);
        personCache.put(39, p39);
        personCache.put(40, p40);
        personCache.put(41, p41);
        personCache.put(42, p42);
        personCache.put(43, p43);
        personCache.put(44, p44);
        personCache.put(45, p45);
        personCache.put(46, p46);
        personCache.put(47, p47);
        personCache.put(48, p48);
        personCache.put(49, p49);
        personCache.put(50, p50);
        personCache.put(51, p51);
        personCache.put(52, p52);
        personCache.put(53, p53);
        personCache.put(54, p54);
        personCache.put(55, p55);
        personCache.put(56, p56);
        personCache.put(57, p57);
        personCache.put(58, p58);
        personCache.put(59, p59);
        personCache.put(60, p60);
        personCache.put(61, p61);
        personCache.put(62, p62);
        personCache.put(63, p63);
        personCache.put(64, p64);
        personCache.put(65, p65);
        personCache.put(66, p66);
        personCache.put(67, p67);
        personCache.put(68, p68);
        personCache.put(69, p69);
        personCache.put(70, p70);
        personCache.put(71, p71);
        personCache.put(72, p72);
        personCache.put(73, p73);
        personCache.put(74, p74);
        personCache.put(75, p75);
        personCache.put(76, p76);
        personCache.put(77, p77);
        personCache.put(78, p78);
        personCache.put(79, p79);
        personCache.put(80, p80);
        personCache.put(81, p81);
        personCache.put(82, p82);
        personCache.put(83, p83);
        personCache.put(84, p84);
        personCache.put(85, p85);
        personCache.put(86, p86);
        personCache.put(87, p87);
        personCache.put(88, p88);
        personCache.put(89, p89);
        personCache.put(90, p90);
        personCache.put(91, p91);
        personCache.put(92, p92);
        personCache.put(93, p93);
        personCache.put(94, p94);
        personCache.put(95, p95);
        personCache.put(96, p96);
        personCache.put(97, p97);
        personCache.put(98, p98);
        personCache.put(99, p99);
        personCache.put(100, p100);

        // Products
        Product pr1 = new Product(1, "Oat", 1.0f, 100, 0);
        Product pr2 = new Product(2, "Sugar", 2.0f, 100, 0);
        Product pr3 = new Product(3, "Oil", 12.0f, 100, 0);
        Product pr4 = new Product(4, "Bacon", 6.0f, 100, 0);
        Product pr5 = new Product(5, "Gold", 112.0f, 100, 0);
        Product pr6 = new Product(6, "Silver", 11.0f, 100, 0);
        Product pr7 = new Product(7, "Oil", 52.0f, 100, 0);
        Product pr8 = new Product(8, "Jam", 3.0f, 100, 0);
        Product pr9 = new Product(9, "Cheery", 8.0f, 100, 0);
        Product pr10 = new Product(10, "Wood", 1.0f, 100, 0);
        productCache.put(1, pr1);
        productCache.put(2, pr2);
        productCache.put(3, pr3);
        productCache.put(4, pr4);
        productCache.put(5, pr5);
        productCache.put(6, pr6);
        productCache.put(7, pr7);
        productCache.put(8, pr8);
        productCache.put(9, pr9);
        productCache.put(10, pr10);

    }

    private static void print(String msg, Iterable<?> col) {
        if (msg != null)
            System.out.println(">>> " + msg);

        print(col);
    }

    /**
     * Prints collection items.
     *
     * @param col Collection.
     */
    private static void print(Iterable<?> col) {
        for (Object next : col) {
            if (next instanceof Iterable)
                print((Iterable<?>)next);
            else
                System.out.println(">>>     " + next);
        }
    }

}
