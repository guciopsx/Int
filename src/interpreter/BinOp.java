package interpreter;

public class BinOp implements AST {
    AST left,right;
    Token op;
    BinOp(AST left, Token op, AST right){
        this.left=left;
        this.op=op;
        this.right=right;
    }
}
