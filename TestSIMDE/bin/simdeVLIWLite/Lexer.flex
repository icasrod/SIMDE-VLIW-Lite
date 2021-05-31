/****************************************************************************
Lexer.flex
ParserWizard generated Lex file.

Date: mi�rcoles, 27 de agosto de 2003
****************************************************************************/
package simdeVLIWLite;
%%

%public
%class SIMDELexer

%type Tokens
%unicode



H			= [A-Fa-f0-9]
D			= [0-9]
E			= [Ee][+-]?{D}+
id			= [A-Za-z_][A-Za-z0-9_]*
espacio		= [ \t\v\f]
direccion	= [+-]?{D}*"("[Rr]{D}+")"

%%

^{D}+			{	/* Esto es el n�mero de l�neas del fichero */
					return Tokens.LEXNLINEAS;
				}

#[+-]?{D}+		{ return Tokens.LEXINMEDIATO; }
[Ff]{D}+		{ return Tokens.LEXREGFP; }
[Rr]{D}+		{ return Tokens.LEXREGGP; }
{id}			{ return Tokens.LEXID; }
{id}":"			{ return Tokens.LEXETIQUETA; }
{direccion}		{ return Tokens.LEXDIRECCION; }

"//".*			{ /* Comentario */ }
{espacio}+		{ /* Espacio en blanco */ }
[^]			{ /* Cosas extra�as y retornos de carro */ }
