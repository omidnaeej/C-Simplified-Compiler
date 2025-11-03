grammar SimpleLang;

@header{
    import main.ast.nodes.*;
    import main.ast.nodes.externaldeclaration.*;
    import main.ast.nodes.declarations.*;
    import main.ast.nodes.statements.*;
    import main.ast.nodes.expression.*;
}

program returns [Program programRet]
    : {$programRet = new Program();}
    (t = translationUnit {$programRet.setTranslationUnit($t.translationUnitRet); })?
    EOF ;

translationUnit returns [TranslationUnit translationUnitRet]
    : {$translationUnitRet = new TranslationUnit(); }
    (e = externalDeclaration {$translationUnitRet.addExternalDeclaration($e.externalDeclarationRet); })+
    ;

externalDeclaration returns [ExternalDeclaration externalDeclarationRet]
    : f=functionDefinition {$externalDeclarationRet = $f.functionDefinitionRet;}
    | d=declaration {$externalDeclarationRet = $d.declarationRet;}
    | s=Semi {$externalDeclarationRet = new EmptyDeclaration();}
    ;

functionDefinition returns [FunctionDefinition functionDefinitionRet]
    : {$functionDefinitionRet = new FunctionDefinition(); }
    (ds = declarationSpecifiers {$functionDefinitionRet.setDeclarationSpecifiers($ds.declarationSpecifiersRet);})?
    d = declarator {$functionDefinitionRet.setDeclarator($d.declaratorRet) ;}
    (dl = declarationList {$functionDefinitionRet.setDeclarationList($dl.declarationListRet) ;})?
    c = compoundStatement {
        $functionDefinitionRet.setCompoundStatement($c.compoundStatementRet);
        $functionDefinitionRet.setLine($ds.declarationSpecifiersRet.getLine());
        }
    ;

declarationList returns [DeclarationList declarationListRet]
    : {$declarationListRet = new DeclarationList();}
    (d = declaration {$declarationListRet.addDeclaration($d.declarationRet);})+
    ;

expression returns [Expression expressionRet]
  @init { $expressionRet = new Expression(); }  // Initialize before any use
   :
   id = Identifier {$expressionRet.setIdentifier($id.text); $expressionRet.setLine($id.line); $expressionRet.setExpressionType("Identifier");}
   | c = Constant {$expressionRet.setConstant($c.text); $expressionRet.setLine($c.line); $expressionRet.setExpressionType("Constant");}
   | (sl = StringLiteral {$expressionRet.addStringLiteral($sl.text); } )+ {$expressionRet.setLine($sl.line); $expressionRet.setExpressionType("String");}
  | lpn = LeftParen e = expression RightParen {
        $expressionRet.addExpressions($e.expressionRet);
        $expressionRet.setLine($lpn.line);
        $expressionRet.setExpressionType("(expression)");
    }
  | lpn = LeftParen t = typeName RightParen LeftBrace i = initializerList (Comma)? RightBrace {
        $expressionRet.setTypeName($t.typeNameRet);
        $expressionRet.setLine($lpn.line);
        $expressionRet.setInitializerList($i.initializerListRet);
        $expressionRet.setExpressionType("initializing typeName");
    }
  | e = expression lbt = LeftBracket e2 = expression RightBracket {
       Expression wrapper = new Expression();
       wrapper.addExpressions($e.expressionRet);
       $expressionRet = wrapper;
       $expressionRet.addExpressions($e2.expressionRet);
       $expressionRet.setLine($lbt.line);
       $expressionRet.setExpressionType("Array indexing");
  }                               // Array indexing
  | e = expression lpn= LeftParen
  {
       boolean tf = false;
  }
  (a = argumentExpressionList {tf = true;})? RightParen
  {
       Expression wrapper = new Expression();
       wrapper.addExpressions($e.expressionRet);
       $expressionRet = wrapper;
       if (tf) {
           $expressionRet.setArgumentExpressionList($a.argumentExpressionListRet);
       }
       $expressionRet.setExpressionType("Function call");
       $expressionRet.setLine($lpn.line);
  }                      // Function call
  | e = expression o = PlusPlus {                                                           // Postfix increment
       Expression wrapper = new Expression();
       wrapper.addExpressions($e.expressionRet);
       $expressionRet = wrapper;
       $expressionRet.setOperatorType($o.text);
       $expressionRet.setLine($o.line);
       $expressionRet.setExpressionType("Postfix increment");
  }
  | e = expression o = MinusMinus {                                                         // Postfix decrement
        Expression wrapper = new Expression();
        wrapper.addExpressions($e.expressionRet);
        $expressionRet = wrapper;
        $expressionRet.setOperatorType($o.text);
        $expressionRet.setLine($o.line);
        $expressionRet.setExpressionType("Postfix decrement");
   }
  | (po= (PlusPlus | MinusMinus | Sizeof) {
            $expressionRet.addprefixOperator($po.text);
            $expressionRet.setLine($po.line);
                if ($po.text != "sizeof") {
                    $expressionRet.setOperatorType($po.text);
                }
            } )*
     (                                          // Prefix operators (zero or more)
         id= Identifier {
                $expressionRet.setIdentifier($id.text);
                $expressionRet.setLine($id.line);
                if ($po.text != "sizeof") {
                    $expressionRet.setOperatorType($po.text);
                }
                $expressionRet.setExpressionType("Identifier with Prefix operators");
            }
       | c= Constant {
                $expressionRet.setConstant($c.text);
                $expressionRet.setLine($c.line);
                $expressionRet.setExpressionType("Constant with Prefix operators");
            }
       | (sl= StringLiteral {$expressionRet.addStringLiteral($sl.text); })+ {$expressionRet.setExpressionType("String with Prefix operators");}
       | LeftParen e= expression RightParen {
                $expressionRet.addExpressions($e.expressionRet);
                $expressionRet.setExpressionType("(expression) with Prefix operators");
            }
       | LeftParen t= typeName RightParen LeftBrace i= initializerList (Comma)? RightBrace {
                $expressionRet.setTypeName($t.typeNameRet);
                $expressionRet.setInitializerList($i.initializerListRet);
                $expressionRet.setExpressionType("initializing typeName witfh Prefix operators");
            }
       | uo= unaryOperator ce= castExpression {
                $expressionRet.setUnaryOperator($uo.unaryOperatorRet);
                $expressionRet.setCastExpression($ce.castExpressionRet);
                $expressionRet.setOperatorType($uo.text);
                $expressionRet.setLine($uo.unaryOperatorRet.getLine());
                $expressionRet.setExpressionType("Unary Operator with Cast expression");
       }
       | Sizeof LeftParen t= typeName RightParen {
                $expressionRet.setTypeName($t.typeNameRet);
                $expressionRet.setExpressionType("Sizeof");
            }
     )

  | lpn= LeftParen t = typeName RightParen ce = castExpression {                                  // Cast expression
        $expressionRet.setTypeName($t.typeNameRet);
        $expressionRet.setCastExpression($ce.castExpressionRet);
        $expressionRet.setLine($lpn.line);
        $expressionRet.setExpressionType("Cast expression");
        }
  | e= expression o = (Star | Div | Mod) e2 = expression {                                     // Multiplicative
       Expression wrapper = new Expression();
       wrapper.addExpressions($e.expressionRet);
       $expressionRet = wrapper;
       $expressionRet.addExpressions($e2.expressionRet);
       $expressionRet.setOperatorType($o.text);
       $expressionRet.setLine($o.line);
       $expressionRet.setExpressionType("Multiplicative");
  }
  | e = expression o = (Plus | Minus) e2= expression {                                          // Additive
       Expression wrapper = new Expression();
       wrapper.addExpressions($e.expressionRet);
       $expressionRet = wrapper;
       $expressionRet.addExpressions($e2.expressionRet);
       $expressionRet.setOperatorType($o.text);
       $expressionRet.setLine($o.line);
       $expressionRet.setExpressionType("Additive");
  }
  | e= expression o = (LeftShift | RightShift) e2= expression  {                              // Shift
       Expression wrapper = new Expression();
       wrapper.addExpressions($e.expressionRet);
       $expressionRet = wrapper;
       $expressionRet.addExpressions($e2.expressionRet);
       $expressionRet.setOperatorType($o.text);
       $expressionRet.setLine($o.line);
       $expressionRet.setExpressionType("Shift");
  }
  | e= expression o = (Less | Greater | LessEqual | GreaterEqual) e2 = expression   {          // Relational
       Expression wrapper = new Expression();
       wrapper.addExpressions($e.expressionRet);
       $expressionRet = wrapper;
       $expressionRet.addExpressions($e2.expressionRet);
       $expressionRet.setOperatorType($o.text);
       $expressionRet.setLine($o.line);
       $expressionRet.setExpressionType("Relational");
  }
  | e= expression o= (Equal | NotEqual) e2= expression {                                     // Equality
       Expression wrapper = new Expression();
       wrapper.addExpressions($e.expressionRet);
       $expressionRet = wrapper;
       $expressionRet.addExpressions($e2.expressionRet);
       $expressionRet.setOperatorType($o.text);
       $expressionRet.setLine($o.line);
       $expressionRet.setExpressionType("Equality");
  }
  | e= expression o=And e2= expression {                                                    // Bitwise AND
       Expression wrapper = new Expression();
       wrapper.addExpressions($e.expressionRet);
       $expressionRet = wrapper;
       $expressionRet.addExpressions($e2.expressionRet);
       $expressionRet.setOperatorType($o.text);
       $expressionRet.setLine($o.line);
       $expressionRet.setExpressionType("Bitwise AND");
  }
  | e= expression o= Xor e2= expression   {                                                  // Bitwise XOR
       Expression wrapper = new Expression();
       wrapper.addExpressions($e.expressionRet);
       $expressionRet = wrapper;
       $expressionRet.addExpressions($e2.expressionRet);
       $expressionRet.setOperatorType($o.text);
       $expressionRet.setLine($o.line);
       $expressionRet.setExpressionType("Bitwise XOR");
  }
  | e= expression o= Or e2= expression   {                                                   // Bitwise OR
       Expression wrapper = new Expression();
       wrapper.addExpressions($e.expressionRet);
       $expressionRet = wrapper;
       $expressionRet.addExpressions($e2.expressionRet);
       $expressionRet.setOperatorType($o.text);
       $expressionRet.setLine($o.line);
       $expressionRet.setExpressionType("Bitwise OR");
  }
  | e= expression o= AndAnd e2= expression {                                                 // Logical AND
       Expression wrapper = new Expression();
       wrapper.addExpressions($e.expressionRet);
       $expressionRet = wrapper;
       $expressionRet.addExpressions($e2.expressionRet);
       $expressionRet.setOperatorType($o.text);
       $expressionRet.setLine($o.line);
       $expressionRet.setExpressionType("Logical AND");
  }
  | e= expression o= OrOr e2= expression {                                                   // Logical OR
       Expression wrapper = new Expression();
       wrapper.addExpressions($e.expressionRet);
       $expressionRet = wrapper;
       $expressionRet.addExpressions($e2.expressionRet);
       $expressionRet.setOperatorType($o.text);
       $expressionRet.setLine($o.line);
       $expressionRet.setExpressionType("Logical OR");
  }
  | e= expression Question e1= expression Colon e2= expression {                              // Conditional operator
       Expression wrapper = new Expression();
       wrapper.addExpressions($e.expressionRet);
       $expressionRet = wrapper;
       $expressionRet.addExpressions($e1.expressionRet);
       $expressionRet.addExpressions($e2.expressionRet);
       $expressionRet.setExpressionType("Conditional operator");
  }
  | e= expression ao= assignmentOperator e2= expression {                                     // Assignment
       Expression wrapper = new Expression();
       wrapper.addExpressions($e.expressionRet);
       $expressionRet = wrapper;
       $expressionRet.addExpressions($e2.expressionRet);
       $expressionRet.setAssignmentOperator($ao.assignmentOperatorRet);
       $expressionRet.setLine($ao.assignmentOperatorRet.getLine());
       $expressionRet.setExpressionType("Assignment");
       $expressionRet.setOperatorType("Assignments");
  }
  | e= expression {
       Expression wrapper = new Expression();
       wrapper.addExpressions($e.expressionRet);
       $expressionRet = wrapper;
       $expressionRet.setExpressionType("Comma operator");
  } (cma = Comma e2= expression {
                $expressionRet.addExpressions($e2.expressionRet);
                $expressionRet.setLine($cma.line);
     })+                                                // Comma operator
  ;

argumentExpressionList returns [ArgumentExpressionList argumentExpressionListRet]
  : {$argumentExpressionListRet = new ArgumentExpressionList(); }
  e= expression {$argumentExpressionListRet.addExpression($e.expressionRet); }
  (Comma ee = expression {$argumentExpressionListRet.addExpression($ee.expressionRet); })*
  ;

unaryOperator returns [UnaryOperator unaryOperatorRet]
  : {$unaryOperatorRet = new UnaryOperator(); }
  x= (And | Star | Plus | Minus | Tilde | Not)
  {
        $unaryOperatorRet.setType($x.text);
        $unaryOperatorRet.setLine($x.line);
  }
  ;

castExpression returns [CastExpression castExpressionRet]
  @init {$castExpressionRet = new CastExpression(); }
  :
  LeftParen t = typeName RightParen c = castExpression {
        $castExpressionRet.setTypeName($t.typeNameRet);
        $castExpressionRet.setCastExpression($c.castExpressionRet);
   }
  | e = expression {$castExpressionRet.setExpression($e.expressionRet); }
  | d= DigitSequence {$castExpressionRet.setDigitSequence($d.text); }
  ;

assignmentOperator returns [AssignmentOperator assignmentOperatorRet]
  : {$assignmentOperatorRet = new AssignmentOperator(); }
  x = (Assign | StarAssign | DivAssign | ModAssign | PlusAssign
            | MinusAssign | LeftShiftAssign | RightShiftAssign | AndAssign | XorAssign | OrAssign
       )
  {$assignmentOperatorRet.setType($x.text); $assignmentOperatorRet.setLine($x.line); }
  ;

declaration returns [Declaration declarationRet]
    : {$declarationRet = new Declaration();}
    d = declarationSpecifiers {$declarationRet.setDeclarationSpecifiers($d.declarationSpecifiersRet); }
    (i = initDeclaratorList {$declarationRet.setInitDeclaratorList($i.initDeclaratorListRet); })?
    Semi
    ;

declarationSpecifiers returns [DeclarationSpecifiers declarationSpecifiersRet]
    : {$declarationSpecifiersRet = new DeclarationSpecifiers(); }
    (d=declarationSpecifier {$declarationSpecifiersRet.addDeclarationSpecifier($d.declarationSpecifierRet); })+
    {$declarationSpecifiersRet.setLine($d.declarationSpecifierRet.getLine());}
    ;

declarationSpecifier returns [DeclarationSpecifier declarationSpecifierRet]
    @init { $declarationSpecifierRet = new DeclarationSpecifier(); } // Initialize here
    : td= Typedef { $declarationSpecifierRet.setType("typedef"); $declarationSpecifierRet.setLine($td.line); }
    | t=typeSpecifier {
//        if ($t.typeSpecifierRet != null) {
            $declarationSpecifierRet.setType("typeSpecifier");
            $declarationSpecifierRet.setLine($t.typeSpecifierRet.getLine());
            $declarationSpecifierRet.setTypeSpecifier($t.typeSpecifierRet);
//        }
      }
    | c= Const { $declarationSpecifierRet.setType("const"); $declarationSpecifierRet.setLine($c.line);}
    ;

initDeclaratorList returns [InitDeclaratorList initDeclaratorListRet]
    : {$initDeclaratorListRet = new InitDeclaratorList(); }
    i = initDeclarator {$initDeclaratorListRet.addInitDeclarator($i.initDeclaratorRet);}
    (Comma o = initDeclarator {$initDeclaratorListRet.addInitDeclarator($o.initDeclaratorRet);})*
    ;

initDeclarator returns [InitDeclarator initDeclaratorRet]
    : {$initDeclaratorRet = new InitDeclarator(); }
    d = declarator {$initDeclaratorRet.setDeclarator($d.declaratorRet);}
    (Assign i = initializer {$initDeclaratorRet.setInitializer($i.initializerRet);})?
    ;

typeSpecifier returns [TypeSpecifier typeSpecifierRet]
    : v = (Void | Char | Short | Int | Long | Float | Double | Signed | Unsigned | Bool)
      { $typeSpecifierRet = new TypeSpecifier();
        $typeSpecifierRet.setType($v.text);
        $typeSpecifierRet.setLine($v.getLine());
        $typeSpecifierRet.setIdentifier(false);
      }
    | id = Identifier
      { $typeSpecifierRet = new TypeSpecifier();
        $typeSpecifierRet.setType($id.text);
        $typeSpecifierRet.setLine($id.getLine());
        $typeSpecifierRet.setIdentifier(true);
      }
    ;

specifierQualifierList returns [SpecifierQualifierList specifierQualifierListRet]
    : {$specifierQualifierListRet = new SpecifierQualifierList();}
    (t = typeSpecifier {$specifierQualifierListRet.setTypeSpecifier($t.typeSpecifierRet);} | Const)
    (s = specifierQualifierList {$specifierQualifierListRet.setNestedSpecifierQualifierList($s.specifierQualifierListRet);})?
    ;

declarator returns [Declarator declaratorRet]
    : {$declaratorRet = new Declarator();}
    (p = pointer {$declaratorRet.setPointer($p.pointerRet); })?
    d = directDeclarator {$declaratorRet.setDirectDeclarator($d.directDeclaratorRet); }
    ;

directDeclarator returns [DirectDeclarator directDeclaratorRet]
    : {$directDeclaratorRet = new DirectDeclarator(); }
      i = Identifier {$directDeclaratorRet.setIdentifier($i.text); $directDeclaratorRet.setLine($i.line);}
    | LeftParen d = declarator RightParen {$directDeclaratorRet.setDeclarator($d.declaratorRet);}
    | dd = directDeclarator LeftBracket {
              DirectDeclarator wrapper = new DirectDeclarator();
              wrapper.setNestedDirectDeclarator($dd.directDeclaratorRet);
              $directDeclaratorRet = wrapper;
          }
          (e = expression {$directDeclaratorRet.setExpression($e.expressionRet);} )? RightBracket
    | dd = directDeclarator LeftParen {
          // Create wrapper node
          DirectDeclarator wrapper = new DirectDeclarator();
          wrapper.setNestedDirectDeclarator($dd.directDeclaratorRet);
          $directDeclaratorRet = wrapper;
      }
      (p = parameterList { $directDeclaratorRet.setParameterList($p.parameterListRet); }
      | (il = identifierList { $directDeclaratorRet.setIdentifierList($il.identifierListRet); })?
      )
      RightParen
    ;

pointer returns [Pointer pointerRet]
    : {$pointerRet = new Pointer();}
    (
        Star { $pointerRet.addPointerLevel(false); }   // non-const pointer
        | Star (Const)+ { $pointerRet.addPointerLevel(true); }  // const pointer
    )+
    ;

parameterList returns [ParameterList parameterListRet]
    : {$parameterListRet = new ParameterList();}
    p = parameterDeclaration {$parameterListRet.addParameterDeclaration($p.parameterDeclarationRet); }
    (Comma p2 = parameterDeclaration  {$parameterListRet.addParameterDeclaration($p2.parameterDeclarationRet); })*
    ;

parameterDeclaration returns [ParameterDeclaration parameterDeclarationRet]
    : {$parameterDeclarationRet = new ParameterDeclaration();}
    ds = declarationSpecifiers {$parameterDeclarationRet.setDeclarationSpecifiers($ds.declarationSpecifiersRet);}
    (
        d = declarator {$parameterDeclarationRet.setDeclarator($d.declaratorRet);}
        | (ad = abstractDeclarator {$parameterDeclarationRet.setAbstractDeclarator($ad.abstractDeclaratorRet); } )?
    )
    ;

identifierList returns [IdentifierList identifierListRet]
    : {$identifierListRet = new IdentifierList();}
    i = Identifier {$identifierListRet.addIdentifiers($i.text);}
    (Comma i2 = Identifier {$identifierListRet.addIdentifiers($i2.text);})*
    ;

typeName returns [TypeName typeNameRet]
    : {$typeNameRet = new TypeName(); }
    s = specifierQualifierList {$typeNameRet.setSpecifierQualifierList($s.specifierQualifierListRet); }
    (a = abstractDeclarator {$typeNameRet.setAbstractDeclarator($a.abstractDeclaratorRet); })?
    ;

abstractDeclarator returns [AbstractDeclarator abstractDeclaratorRet]
    @init {
       $abstractDeclaratorRet = new AbstractDeclarator();
    } :
    p = pointer {$abstractDeclaratorRet.addPointers($p.pointerRet); }
    | (p2 = pointer {$abstractDeclaratorRet.addPointers($p2.pointerRet); })?
    d = directAbstractDeclarator {$abstractDeclaratorRet.setDirectAbstractDeclarator($d.directAbstractDeclaratorRet); }
    ;

directAbstractDeclarator returns [DirectAbstractDeclarator directAbstractDeclaratorRet]
    : {$directAbstractDeclaratorRet = new DirectAbstractDeclarator(); }
    LeftBracket (e = expression {$directAbstractDeclaratorRet.setExpression($e.expressionRet); })? RightBracket
    | LeftParen  (a = abstractDeclarator {$directAbstractDeclaratorRet.setAbstractDeclarator($a.abstractDeclaratorRet); }
                    | (p = parameterList {$directAbstractDeclaratorRet.setParameterList($p.parameterListRet); })?
                 ) RightParen
    | d = directAbstractDeclarator LeftBracket (e = expression {$directAbstractDeclaratorRet.setExpression($e.expressionRet); })? RightBracket {
           // Create wrapper node
           DirectAbstractDeclarator wrapper = new DirectAbstractDeclarator();
           wrapper.setDirectAbstractDeclarator($d.directAbstractDeclaratorRet);
           $directAbstractDeclaratorRet = wrapper;
       }
    | d = directAbstractDeclarator LeftParen (p = parameterList {$directAbstractDeclaratorRet.setParameterList($p.parameterListRet); })? RightParen {
           // Create wrapper node
           DirectAbstractDeclarator wrapper = new DirectAbstractDeclarator();
           wrapper.setDirectAbstractDeclarator($d.directAbstractDeclaratorRet);
           $directAbstractDeclaratorRet = wrapper;
       }
    ;

initializer returns [Initializer initializerRet]
    @init { $initializerRet = new Initializer(); }
    : e = expression {
          if ($e.expressionRet != null) {
              $initializerRet.setExpression($e.expressionRet);
          }
      }
    | LeftBrace i = initializerList (Comma)? RightBrace {
          if ($i.initializerListRet != null) {
              $initializerRet.setInitializerList($i.initializerListRet);
          }
      }
    ;

initializerList returns [InitializerList initializerListRet]
    : {$initializerListRet = new InitializerList(); }
    (d = designation {$initializerListRet.addDesignation($d.designationRet); })?
    i = initializer {$initializerListRet.addInitializer($i.initializerRet); }
    (Comma
        (dd = designation {$initializerListRet.addDesignation($dd.designationRet); })?
        ii = initializer {$initializerListRet.addInitializer($ii.initializerRet); }
    )*
    ;

designation returns [Designation designationRet]
    : {$designationRet = new Designation(); }
    (d=designator {$designationRet.addDesignator($d.designatorRet); })+
    Assign
    ;

designator returns [Designator designatorRet]
    : {$designatorRet = new Designator(); }
    LeftBracket e = expression RightBracket {$designatorRet.setExpression($e.expressionRet); }
    | Dot i=Identifier {$designatorRet.setIdentifier($i.text); }
    ;

statement returns [Statement statementRet]
    @init { $statementRet = new Statement(); }  // Initialize first
    : c = compoundStatement {
          if ($c.compoundStatementRet != null) {
              $statementRet.setCompoundStatement($c.compoundStatementRet);
              $statementRet.setStmtType("compoundStatement");
          }
      }
    | e = expressionStatement {
          if ($e.expressionStatementRet != null) {
              $statementRet.setExpressionStatement($e.expressionStatementRet);
              $statementRet.setStmtType("expressionStatement");
          }
      }
    | s= selectionStatement {
        if ($s.selectionStatementRet != null) {
            $statementRet.setSelectionStatement($s.selectionStatementRet);
            $statementRet.setStmtType("selectionStatement");
            }
        }
    | i= iterationStatement {
        if ($i.iterationStatementRet != null) {
            $statementRet.setIterationStatement($i.iterationStatementRet);
            $statementRet.setStmtType("iterationStatement");
            }
        }
    | j= jumpStatement {
        if ($j.jumpStatementRet != null) {
            $statementRet.setJumpStatement($j.jumpStatementRet);
            $statementRet.setStmtType("jumpStatement");
            }
        }
    ;

compoundStatement returns [CompoundStatement compoundStatementRet]
    : {$compoundStatementRet = new CompoundStatement(); }
    LeftBrace
    ((b = blockItem {$compoundStatementRet.addBlockItem($b.blockItemRet); })+)?
    RightBrace
    ;

blockItem returns [BlockItem blockItemRet]
    @init { $blockItemRet = new BlockItem(); }  // Initialize before any use
    : s = statement {
          if ($s.statementRet != null) {
              $blockItemRet.setStatement($s.statementRet);
          }
      }
    | d = declaration {
          if ($d.declarationRet != null) {
              $blockItemRet.setDeclaration($d.declarationRet);
          }
      }
    ;

expressionStatement returns [ExpressionStatement expressionStatementRet]
    : {$expressionStatementRet = new ExpressionStatement(); }
    (e = expression {$expressionStatementRet.setExpression($e.expressionRet); })?
    Semi
    ;

selectionStatement returns [SelectionStatement selectionStatementRet]
    : {$selectionStatementRet = new SelectionStatement(); }
    i=If LeftParen e = expression RightParen s= statement {
        $selectionStatementRet.setExpression($e.expressionRet);
        $selectionStatementRet.setIf_statement($s.statementRet);
        $selectionStatementRet.setLine($i.line);
    }
    (els = Else es = statement {$selectionStatementRet.setElse_statement($es.statementRet); $selectionStatementRet.setElse_line($els.line);})?
    ;

iterationStatement returns [IterationStatement iterationStatementRet]
    @init { $iterationStatementRet = new IterationStatement(); }  // Initialize first
    : wle = While LeftParen e = expression RightParen s = statement {
          $iterationStatementRet.setExpression($e.expressionRet);
          $iterationStatementRet.setStatement($s.statementRet);
          $iterationStatementRet.setLoopType("while");
          $iterationStatementRet.setLine($wle.line);
      }
    | d = Do s = statement While LeftParen e = expression RightParen Semi {
          $iterationStatementRet.setExpression($e.expressionRet);
          $iterationStatementRet.setStatement($s.statementRet);
          $iterationStatementRet.setLoopType("do-while");
          $iterationStatementRet.setLine($d.line);
      }
    | fr = For LeftParen f = forCondition RightParen s = statement {
          if ($f.forConditionRet != null) {
              $iterationStatementRet.setForCondition($f.forConditionRet);
          }
          $iterationStatementRet.setStatement($s.statementRet);
          $iterationStatementRet.setLoopType("for");
          $iterationStatementRet.setLine($fr.line);

      }
    ;

forCondition returns [ForCondition forConditionRet]
    : {$forConditionRet = new ForCondition(); }
    (
        f = forDeclaration {$forConditionRet.setForDeclaration($f.forDeclarationRet);}
        | (e = expression {$forConditionRet.setExpression($e.expressionRet);})?
    )
    Semi (fe = forExpression {$forConditionRet.setForExpression($fe.forExpressionRet);} )?
    Semi (fe2 = forExpression  {$forConditionRet.setForExpression2($fe2.forExpressionRet);})?
    ;

forDeclaration returns [ForDeclaration forDeclarationRet]
    : {$forDeclarationRet = new ForDeclaration(); }
    d = declarationSpecifiers {$forDeclarationRet.setDeclarationSpecifiers($d.declarationSpecifiersRet); }
    (i = initDeclaratorList {$forDeclarationRet.setInitDeclaratorList($i.initDeclaratorListRet); })?
    ;

forExpression returns [ForExpression forExpressionRet]
    : {$forExpressionRet = new ForExpression(); }
    e = expression {$forExpressionRet.addExpression($e.expressionRet); }
    (Comma eo = expression {$forExpressionRet.addExpression($eo.expressionRet); })*
    ;

jumpStatement returns [JumpStatement jumpStatementRet]
    : {$jumpStatementRet = new JumpStatement(); }
    ( t= (Continue | Break | Return) {$jumpStatementRet.setJumpType($t.text); $jumpStatementRet.setLine($t.line);}
    (e=expression {$jumpStatementRet.setExpression($e.expressionRet); })? )
    Semi
    ;

Break                 : 'break'                 ;
Char                  : 'char'                  ;
Const                 : 'const'                 ;
Continue              : 'continue'              ;
Do                    : 'do'                    ;
Double                : 'double'                ;
Else                  : 'else'                  ;
Float                 : 'float'                 ;
For                   : 'for'                   ;
If                    : 'if'                    ;
Int                   : 'int'                   ;
Long                  : 'long'                  ;
Return                : 'return'                ;
Short                 : 'short'                 ;
Signed                : 'signed'                ;
Sizeof                : 'sizeof'                ;
Switch                : 'switch'                ;
Typedef               : 'typedef'               ;
Unsigned              : 'unsigned'              ;
Void                  : 'void'                  ;
While                 : 'while'                 ;
Bool                  : 'bool'                  ;
LeftParen             : '('                     ;
RightParen            : ')'                     ;
LeftBracket           : '['                     ;
RightBracket          : ']'                     ;
LeftBrace             : '{'                     ;
RightBrace            : '}'                     ;
Less                  : '<'                     ;
LessEqual             : '<='                    ;
Greater               : '>'                     ;
GreaterEqual          : '>='                    ;
LeftShift             : '<<'                    ;
RightShift            : '>>'                    ;
Plus                  : '+'                     ;
PlusPlus              : '++'                    ;
Minus                 : '-'                     ;
MinusMinus            : '--'                    ;
Star                  : '*'                     ;
Div                   : '/'                     ;
Mod                   : '%'                     ;
And                   : '&'                     ;
Or                    : '|'                     ;
AndAnd                : '&&'                    ;
OrOr                  : '||'                    ;
Xor                   : '^'                     ;
Not                   : '!'                     ;
Tilde                 : '~'                     ;
Question              : '?'                     ;
Colon                 : ':'                     ;
Semi                  : ';'                     ;
Comma                 : ','                     ;
Assign                : '='                     ;
StarAssign            : '*='                    ;
DivAssign             : '/='                    ;
ModAssign             : '%='                    ;
PlusAssign            : '+='                    ;
MinusAssign           : '-='                    ;
LeftShiftAssign       : '<<='                   ;
RightShiftAssign      : '>>='                   ;
AndAssign             : '&='                    ;
XorAssign             : '^='                    ;
OrAssign              : '|='                    ;
Equal                 : '=='                    ;
NotEqual              : '!='                    ;
Arrow                 : '->'                    ;
Dot                   : '.'                     ;

Identifier
    : IdentifierNondigit (IdentifierNondigit | Digit)* ;

fragment IdentifierNondigit
    : Nondigit | UniversalCharacterName ;

fragment Nondigit
    : [a-zA-Z_] ;

fragment Digit
    : [0-9] ;

fragment UniversalCharacterName
    : '\\u' HexQuad | '\\U' HexQuad HexQuad ;

fragment HexQuad
    : HexadecimalDigit HexadecimalDigit HexadecimalDigit HexadecimalDigit ;

Constant
    : IntegerConstant | FloatingConstant | CharacterConstant ;

fragment IntegerConstant
    : DecimalConstant IntegerSuffix?
    | OctalConstant IntegerSuffix?
    | HexadecimalConstant IntegerSuffix?
    | BinaryConstant ;

fragment BinaryConstant
    : '0' [bB] [0-1]+ ;

fragment DecimalConstant
    : NonzeroDigit Digit* ;

fragment OctalConstant
    : '0' OctalDigit* ;

fragment HexadecimalConstant
    : HexadecimalPrefix HexadecimalDigit+ ;

fragment HexadecimalPrefix
    : '0' [xX] ;

fragment NonzeroDigit
    : [1-9] ;

fragment OctalDigit
    : [0-7] ;

fragment HexadecimalDigit
    : [0-9a-fA-F] ;

fragment IntegerSuffix
    : UnsignedSuffix LongSuffix? | UnsignedSuffix LongLongSuffix | LongSuffix UnsignedSuffix? | LongLongSuffix UnsignedSuffix? ;

fragment UnsignedSuffix
    : [uU] ;

fragment LongSuffix
    : [lL] ;

fragment LongLongSuffix
    : 'll' | 'LL' ;

fragment FloatingConstant
    : DecimalFloatingConstant | HexadecimalFloatingConstant ;

fragment DecimalFloatingConstant
    : FractionalConstant ExponentPart? FloatingSuffix? | DigitSequence ExponentPart FloatingSuffix? ;

fragment HexadecimalFloatingConstant
    : HexadecimalPrefix (HexadecimalFractionalConstant | HexadecimalDigitSequence) BinaryExponentPart FloatingSuffix? ;

fragment FractionalConstant
    : DigitSequence? Dot DigitSequence | DigitSequence Dot ;

fragment ExponentPart
    : [eE] Sign? DigitSequence ;

fragment Sign
    : [+-] ;

DigitSequence
    : Digit+ ;

fragment HexadecimalFractionalConstant
    : HexadecimalDigitSequence? Dot HexadecimalDigitSequence | HexadecimalDigitSequence Dot ;

fragment BinaryExponentPart
    : [pP] Sign? DigitSequence ;

fragment HexadecimalDigitSequence
    : HexadecimalDigit+ ;

fragment FloatingSuffix
    : [flFL] ;

fragment CharacterConstant
    : '\'' CCharSequence '\'' | 'L\'' CCharSequence '\''| 'u\'' CCharSequence '\'' | 'U\'' CCharSequence '\''
    ;

fragment CCharSequence
    : CChar+ ;

fragment CChar
    : ~['\\\r\n] | EscapeSequence ;

fragment EscapeSequence
    : SimpleEscapeSequence | OctalEscapeSequence | HexadecimalEscapeSequence | UniversalCharacterName ;

fragment SimpleEscapeSequence
    : '\\' ['"?abfnrtv\\] ;

fragment OctalEscapeSequence
    : '\\' OctalDigit OctalDigit? OctalDigit? ;

fragment HexadecimalEscapeSequence
    : '\\x' HexadecimalDigit+ ;

StringLiteral
    : EncodingPrefix? '"' SCharSequence? '"' ;

fragment EncodingPrefix
    : 'u8' | 'u' | 'U' | 'L' ;

fragment SCharSequence
    : SChar+ ;

fragment SChar
    : ~["\\\r\n] | EscapeSequence | '\\\n' | '\\\r\n' ;

MultiLineMacro
    : '#' (~[\n]*? '\\' '\r'? '\n')+ ~ [\n]+ -> channel(HIDDEN) ;

Directive
    : '#' ~[\n]* -> channel(HIDDEN) ;

Whitespace
    : [ \t]+ -> channel(HIDDEN) ;

Newline
    : ('\r' '\n'? | '\n') -> channel(HIDDEN) ;

BlockComment
    : '/*' .*? '*/' -> channel(HIDDEN) ;

LineComment
    : '//' ~[\r\n]* -> channel(HIDDEN) ;