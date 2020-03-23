package com.example.adam.shackapp;

public class Piece {
    String type;
    String color;
    int[] moves;
    int currentPosition;

    public Piece(String type, String color, int initialPosition){
        this.type = type;
        this.color = color;
<<<<<<< HEAD
        this.currentPosition = initialPosition;
        this.moves = getMoves(type);
    }

    public static void main(String[] args){
        Piece piece = new Piece("lopare", "vit", 0);
        for(int i=0; i < piece.moves.length; i++){
        System.out.println(piece.type + " alternativ: " + piece.moves[i]);
        }
    }

=======
//        this.moves = getMoves(type);
    }

public static void main(String[] args){
    System.out.println("lol");
    }
>>>>>>> e8de87f44bbd543bfc5065839854618f49cbd9d9

   private int[] getMoves(String type){
        switch (type){
            case "bonde":
                int[] moves_bonde = {(currentPosition + 8), (currentPosition + 16)};
                //if initialposition = 1:8, kan man gå två steg
            return moves_bonde;
            
            
            case "lopare":
            List<int[]> moves_lopare = new ArrayList<>();
            
            
            
            return moves_lopare;




            return moves_lopare;

                //= {(currentPosition -7), (currentPosition -15), (currentPosition -22), (currentPosition -29)
            
                //    (currentPosition -36, (currentPosition -44), (currentPosition -52), };
                
            case "hast":
                int[] moves_hast = {(currentPosition + 8), (currentPosition + 16), (currentPosition + 6), (currentPosition + 10)};
            return moves_hast;
                
            case "torn":
                int[] moves_torn = {(currentPosition + 8), (currentPosition + 16), (currentPosition + 64)};
            return moves_torn;
                
            case "kung":
                int[] moves_kung = {(currentPosition - 9), (currentPosition -8) , (currentPosition -7), (currentPosition -1), (currentPosition +1),
                (currentPosition +7), (currentPosition +8), (currentPosition +9)};
            return moves_kung;
                
            case "drottning":
                int[] moves_drottning = {(currentPosition + 8), (currentPosition + 16), (currentPosition + 64)};
            return moves_drottning;
                

                default: int[] moves = {};
            return moves;    
            
            
        }

        
    } 
}
