INSERT INTO permissao (codigo, nome) VALUES (1, 'ROLE_CADASTRAR_CIDADE');
INSERT INTO permissao (codigo, nome) VALUES (2, 'ROLE_CADASTRAR_USUARIO');

INSERT INTO grupo_permissao (codigo_grupo, codigo_permissao) VALUES (1,1);
INSERT INTO grupo_permissao (codigo_grupo, codigo_permissao) VALUES (1,2);

INSERT INTO usuario_grupo (codigo_usuario, codigo_grupo) VALUES (
(SELECT codigo FROM usbase.usuario WHERE email = 'admin@usbase.com'), 1); 
