/****************************************************************************
Lexer.flex
ParserWizard generated Lex file.

Date: miércoles, 27 de agosto de 2003
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

^{D}+			{	/* Esto es el número de líneas del fichero */
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
[^]			{ /* Cosas extrañas y retornos de carro */ }
