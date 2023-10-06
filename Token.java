public class Token {
    public static int TIPO_INTEIRO = 0;
    public static int TIPO_REAL = 1;
    public static int TIPO_CHAR = 2;
    public static int TIPO_IDENTIFICADOR = 3;
    public static int TIPO_OPERADOR_RELACIONAL = 4;
    public static int TIPO_OPERADOR_ARITMETICO = 5;
    public static int TIPO_CARACTER_ESPECIAL = 6;
    public static int TIPO_PALAVRA_RESERVADA = 7;
    public static int TIPO_OPERADOR_ATRIBUICAO = 8;
    public static int TIPO_TEXTO = 9;
    public static int TIPO_COMENTARIO = 10;
    
    private int tipo; //tipo do token
    private String lexema; //conteúdo do token
    
    public Token(String lexema, int tipo){
        this.lexema = lexema;
        this.tipo = tipo;
    }
    
    public String getLexema(){
        return this.lexema;
    }
    
    public int getTipo(){
        return this.tipo;
    }
    
    @Override
    public String toString()
    {
        switch(this.tipo){
            case 0:
                return this.lexema + " - Número Inteiro" ;
            case 1:
                return this.lexema + " - Número Decimal";
            case 2:
                return this.lexema + " - Char";
            case 3:
                return this.lexema + " - Identificador";
            case 4:
                return this.lexema + " - Operador Relacional";
            case 5:
                return this.lexema + " - Operador Aritmétrico";
            case 6:
                return this.lexema + " - Símbolo Especial";
            case 7:
                return this.lexema + " - Palavra Reservada";
            case 8:
                return this.lexema + " - Operador de Atribuição";
            case 9:
                return this.lexema + " - Constante de Texto";
            case 10:
                return this.lexema + " - Comentário";
 
        }
        return "";
    }  
}
