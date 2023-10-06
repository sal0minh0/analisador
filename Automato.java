import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Automato{
    private char[] conteudo;
    private int indiceConteudo;
    private int eComentario;


    public Automato(String caminhoCodigoFonte){
        try {
            String conteudoStr;
            conteudoStr = new String(Files.readAllBytes(Paths.get(caminhoCodigoFonte)));
            this.conteudo = conteudoStr.toCharArray();
            this.indiceConteudo = 0;
            this.eComentario = 0;                       
        } catch (IOException ex) {
            ex.printStackTrace();
        }        
    }

    //Retorna próximo char
    private char nextChar(){
        return this.conteudo[this.indiceConteudo++];
    }
    
    //Verifica existe próximo char ou chegou ao final do código fonte
    private boolean hasNextChar(){
        return indiceConteudo < this.conteudo.length;
    }
    
    //Retrocede o índice que aponta para o "char da vez" em uma unidade
    private void back(){
        this.indiceConteudo--;
    }
    
    //Identificar se char é letra minúscula e maiúscula  
    private boolean isLetra(char c){
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') ||
               c == 'á' || c == 'é' || c == 'í' || c == 'ó' || c == 'ú' ||
               c == 'à' || c == 'è' || c == 'ì' || c == 'ò' || c == 'ù' ||
               c == 'â' || c == 'ê' || c == 'î' || c == 'ô' || c == 'û';
    }
    
    //Identificar se char é dígito
    private boolean isDigito(char c){
        return (c >= '0') && (c <= '9');
    }
    
    //Método retorna próximo token válido ou retorna mensagem de erro.
    public Token nextToken(){
        Token token = null;
        char c;
        int estado = 0;
        boolean dentroAspas = false; // Variável para controlar se estamos dentro de aspas duplas
        
        
        StringBuffer lexema = new StringBuffer();
        String verificaPalavraReservada = "";
        while(this.hasNextChar()){
            c = this.nextChar();            
            switch(estado){
                case 0:
                    if(this.eComentario == 1 && c != '/'){
                        if(c == '*'){
                            this.back();
                            this.eComentario = 2;
                        }else if (c == ' '){
                            estado = 19;
                            this.eComentario = 1;
                        }else{
                            this.back();
                            this.eComentario = 0;
                        }
                    }
                    else if(c == ' ' || c == '\t' || c == '\n' || c == '\r' ){ //caracteres de espaço em branco ASCII tradicionais 
                        if (c == ' ' && this.eComentario == 1){
                            estado = 3;
                        }else{
                            estado = 0;
                        }
                    }
                    else if(this.eComentario == 1 && c != '/'){
                        if(c == '*'){
                            this.back();
                            this.eComentario = 2;
                        }else{
                            this.back();
                            this.eComentario = 0;
                        }
                    }
                    
                    else if(this.isLetra(c)){
                        lexema.append(c);
                        verificaPalavraReservada+=c;
                        estado = 1;
                    }
                    else if(this.isDigito(c)){
                        lexema.append(c);
                        estado = 2;
                    }
                    else if(c == ')' || 
                            c == '(' ||
                            c == '{' ||
                            c == '}' ||
                            c == ',' ||
                            c == ';'){
                        lexema.append(c);
                        estado = 5;
                    }else if(c == '+' || c == '-' || c == '*' || c == '/' || c == '%'){
                        if (c == '/' && this.eComentario == 0){
                            this.eComentario++;
                            lexema.append(c);
                            estado = 6;
                        }else if (this.eComentario == 2 && c == '*'){
                            lexema.append(c);
                            estado = 6;
                            this.back(); //
                        }else{
                          lexema.append(c);
                          estado = 6;
                        }
                    }
                     else if((int) c == 39){
                        lexema.append(c);
                        estado = 7;
                    } else if(c == '<'){
                        lexema.append(c);
                        estado = 10;
                    } else if(c == '='){
                        lexema.append(c);
                        estado = 13;
                    } else if(c == '>'){
                        lexema.append(c);
                        estado = 15;
                    }else if(c == '"' && !dentroAspas){
                        lexema.append(c);
                        estado = 17; // Muda para o estado de cadeia de caracteres delimitada por aspas duplas
                        dentroAspas = true;
                    }
                    else if(c == '$'){
                        lexema.append(c);
                        estado = 99;
                        this.back();
                    }else{
                        lexema.append(c);
                        throw new RuntimeException("Erro: token inválido \"" + lexema.toString() + "\"");
                    }
                    break;
                case 1:
                    if(this.isLetra(c) || this.isDigito(c)){
                        lexema.append(c);
                        verificaPalavraReservada+=c;
                        estado = 1;                        
                    }else{
                        this.back();
                        if(verificaPalavraReservada.compareTo("int") == 0 || verificaPalavraReservada.compareTo("float") == 0 ||
                        verificaPalavraReservada.compareTo("char") == 0 || verificaPalavraReservada.compareTo("while") == 0 ||
                        verificaPalavraReservada.compareTo("main") == 0 || verificaPalavraReservada.compareTo("if") == 0 ||
                        verificaPalavraReservada.compareTo("else") == 0 || verificaPalavraReservada.compareTo("scanf" ) == 0 || 
                        verificaPalavraReservada.compareTo("for") == 0 || verificaPalavraReservada.compareTo("return") == 0 || 
                        verificaPalavraReservada.compareTo("boolean") == 0 || verificaPalavraReservada.compareTo("void") == 0){
                            return new Token(lexema.toString(), Token.TIPO_PALAVRA_RESERVADA);    
                        } else{
                            verificaPalavraReservada="";
                            return new Token(lexema.toString(), Token.TIPO_IDENTIFICADOR);  
                        }
                      
                    }
                    break;
                case 2:
                    if(this.isDigito(c)){
                        lexema.append(c);
                        estado = 2;
                    }else if(c == '.'){
                        lexema.append(c);
                        estado = 3;
                    }else{
                        this.back();
                        return new Token(lexema.toString(), Token.TIPO_INTEIRO);
                    }
                    break;
                case 3:
                    if(this.isDigito(c)){
                        lexema.append(c);
                        estado = 4;
                    }else{
                        throw new RuntimeException("Erro: número float inválido \"" + lexema.toString() + "\"");
                    }
                    break;
                case 4:
                    if(this.isDigito(c)){
                        lexema.append(c);
                        estado = 4;
                    }else{
                        this.back();
                        return new Token(lexema.toString(), Token.TIPO_REAL);
                    }
                    break;
                case 5:
                    this.back();
                    return new Token(lexema.toString(), Token.TIPO_CARACTER_ESPECIAL);
                case 6:
                    if(c == '/' && this.eComentario == 1){
                        estado = 19;
                    }
                    else if(c == '*' && this.eComentario == 2){
                        estado = 21;
                        break;
                    }
                    this.back();
                    return new Token(lexema.toString(), Token.TIPO_OPERADOR_ARITMETICO);
                case 7:
                    if(this.isDigito(c) || this.isLetra(c)){
                        lexema.append(c);
                        estado = 8;
                    } else{
                        throw new RuntimeException("Erro: char mal formatado \"" + lexema.toString() + "\"");
                    }
                    break;
                case 8:
                    if((int) c == 39){
                        lexema.append(c);
                        estado = 9;
                    } else{
                        throw new RuntimeException("Erro: char mal formatado \"" + lexema.toString() + "\"");
                    }
                    break;
                case 9:
                    this.back();
                    return new Token(lexema.toString(), Token.TIPO_CHAR);
                case 10:
                    if (c == '>') {
                        lexema.append(c);
                        estado = 11;
                    } else if (c == '=') {
                        lexema.append(c);
                        estado = 12;
                    } else {
                        this.back();
                        return new Token(lexema.toString(), Token.TIPO_OPERADOR_RELACIONAL);
                    }
                    break;
                case 11:
                    this.back();
                    return new Token(lexema.toString(), Token.TIPO_OPERADOR_RELACIONAL);
                case 12:
                    this.back();
                    return new Token(lexema.toString(), Token.TIPO_OPERADOR_RELACIONAL);
                case 13:
                    if (c == '=') {
                        lexema.append(c);
                        estado = 14;
                    } else {
                        this.back();
                        return new Token(lexema.toString(), Token.TIPO_OPERADOR_ATRIBUICAO);
                    }
                    break;
                case 14:
                    this.back();
                    return new Token(lexema.toString(), Token.TIPO_OPERADOR_RELACIONAL);
                case 15:
                    if (c == '=') {
                        lexema.append(c);
                        estado = 16;
                    } else {
                        this.back();
                        return new Token(lexema.toString(), Token.TIPO_OPERADOR_RELACIONAL);
                    }
                    break;
                case 16:
                    this.back();
                    return new Token(lexema.toString(), Token.TIPO_OPERADOR_RELACIONAL);
                case 17:
                    if (c == '"') {
                        lexema.append(c);
                        estado = 18;// Muda para o estado de fim de cadeia de caracteres
                        dentroAspas = false;
                    } else {
                        lexema.append(c);
                        estado = 17;
                    }
                    break;
                case 18:
                    this.back();
                    return new Token(lexema.toString(), Token.TIPO_TEXTO);
                case 19:
                if(isDigito(c)){
                    estado = 3;
                }
                    if(c == '\n' ){
                        lexema.append(c);
                        estado = 20;
                    }else{
                        lexema.append(c);
                        estado = 19;
                    }
                    break;
                case 20:
                    this.back();
                    this.eComentario = 0;
                    return new Token(lexema.toString().trim(), Token.TIPO_COMENTARIO);
                case 21:
                    if (c == '*'){
                        this.back();
                        estado = 20;
                    }else{
                        lexema.append(c);
                        estado = 21;
                    }
                    break;
                
                

            }
        }                
        return token;
    }   
}