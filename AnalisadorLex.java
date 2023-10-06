public class AnalisadorLex {
  
    public static void main(String[] args) {

        Automato lexico = new Automato("./codigo.txt");
        Token t = null;
        while((t = lexico.nextToken()) != null){
            System.out.println(t.toString());
        }


    }
    
}