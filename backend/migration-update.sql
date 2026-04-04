-- Passo 1: Remover a relação atual (precos -> faturamento)
ALTER TABLE precos DROP CONSTRAINT IF EXISTS fk_precos_faturamento;

-- Passo 2: Adicionar a relação invertida (faturamento -> preco)
ALTER TABLE faturamento ADD COLUMN preco_id VARCHAR(36);

-- Passo 3: Migrar os dados existentes (copiar faturamento_id de precos para preco_id de faturamento)
UPDATE faturamento f 
SET preco_id = (
    SELECT p.id 
    FROM precos p 
    WHERE p.faturamento_id = f.id 
    AND p.deleted = FALSE
    LIMIT 1
)
WHERE EXISTS (
    SELECT 1 
    FROM precos p 
    WHERE p.faturamento_id = f.id 
    AND p.deleted = FALSE
);

-- Passo 4: Adicionar a constraint foreign key
ALTER TABLE faturamento ADD CONSTRAINT fk_faturamento_preco 
    FOREIGN KEY (preco_id) REFERENCES precos(id);

-- Passo 5: Remover a coluna antiga da tabela precos
ALTER TABLE precos DROP COLUMN faturamento_id;

-- Passo 6: Adicionar índice para performance
CREATE INDEX idx_faturamento_preco_id ON faturamento(preco_id);
