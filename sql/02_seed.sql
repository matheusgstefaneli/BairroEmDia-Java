-- Dados de exemplo (opcional). Execute após 01_create_tables.sql
INSERT INTO TB_CATEGORIA (ID_CATEGORIA, NOME, DESCRICAO) VALUES (SEQ_CATEGORIA.NEXTVAL, 'Buraco na rua', 'Buracos e falhas no asfalto');
INSERT INTO TB_CATEGORIA (ID_CATEGORIA, NOME, DESCRICAO) VALUES (SEQ_CATEGORIA.NEXTVAL, 'Iluminação pública', 'Postes sem luz ou com defeito');
INSERT INTO TB_CATEGORIA (ID_CATEGORIA, NOME, DESCRICAO) VALUES (SEQ_CATEGORIA.NEXTVAL, 'Lixo acumulado', 'Acúmulo de lixo e entulho');

INSERT INTO TB_MORADOR (ID_MORADOR, NOME, EMAIL) VALUES (SEQ_MORADOR.NEXTVAL, 'Morador Exemplo', 'morador.exemplo@example.com');

INSERT INTO TB_OCORRENCIA (ID_OCORRENCIA, TITULO, DESCRICAO, LOCALIZACAO, PRIORIDADE, STATUS_OCORRENCIA, DATA_CRIACAO, ID_MORADOR, ID_CATEGORIA)
VALUES (SEQ_OCORRENCIA.NEXTVAL, 'Buraco na Rua das Flores', 'Buraco grande em frente ao número 120', 'Rua das Flores, 120', 4, 'ABERTA', SYSTIMESTAMP,
        (SELECT ID_MORADOR FROM TB_MORADOR WHERE EMAIL = 'morador.exemplo@example.com'),
        (SELECT ID_CATEGORIA FROM TB_CATEGORIA WHERE NOME = 'Buraco na rua'));

COMMIT;
