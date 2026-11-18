# Gera os protótipos (wireframes) das telas propostas. NÃO são telas implementadas: o projeto entrega a API REST.
import matplotlib
matplotlib.use("Agg")
import matplotlib.pyplot as plt
from matplotlib.patches import FancyBboxPatch, Rectangle

AZ = "#1F4E79"; CZ = "#F2F2F2"; BORDA = "#888888"

def tela(titulo, rota):
    fig, ax = plt.subplots(figsize=(8, 5.6))
    ax.set_xlim(0, 100); ax.set_ylim(0, 70); ax.axis("off")
    ax.add_patch(Rectangle((0, 0), 100, 70, fc="white", ec=BORDA, lw=1.5))
    ax.add_patch(Rectangle((0, 64), 100, 6, fc="#DDDDDD", ec=BORDA))
    ax.text(2, 67, "●  ●  ●", fontsize=8, va="center", color="#777")
    ax.add_patch(FancyBboxPatch((14, 65), 60, 4, boxstyle="round,pad=0.1", fc="white", ec=BORDA))
    ax.text(16, 67, rota, fontsize=8, va="center", color="#444")
    ax.add_patch(Rectangle((0, 56), 100, 8, fc=AZ, ec=AZ))
    ax.text(3, 60, "Bairro em Dia", color="white", fontsize=13, weight="bold", va="center")
    for i, t in enumerate(["Ocorrências", "Nova ocorrência", "Moradores", "Categorias"]):
        ax.text(40 + i * 15, 60, t, color="white", fontsize=7.5, va="center")
    ax.text(3, 52, titulo, fontsize=12, weight="bold", va="center", color="#222")
    ax.text(97, 1.5, "PROTÓTIPO (não implementado)", fontsize=7, ha="right", color="#B00020", style="italic")
    return fig, ax

def campo(ax, x, y, w, rotulo, valor="", h=5):
    ax.text(x, y + h + 1.2, rotulo, fontsize=8, color="#333")
    ax.add_patch(Rectangle((x, y), w, h, fc="white", ec=BORDA))
    ax.text(x + 1, y + h / 2, valor, fontsize=8, va="center", color="#777")

def botao(ax, x, y, w, texto, cor=AZ, h=5):
    ax.add_patch(FancyBboxPatch((x, y), w, h, boxstyle="round,pad=0.2", fc=cor, ec=cor))
    ax.text(x + w / 2, y + h / 2, texto, color="white", fontsize=8, ha="center", va="center", weight="bold")

# ---- Tela 1: lista ----
fig, ax = tela("Ocorrências do bairro", "https://bairro-em-dia/ocorrencias")
campo(ax, 3, 38, 22, "Status", "Todos  ▾")
campo(ax, 28, 38, 22, "Categoria", "Todas  ▾")
botao(ax, 80, 38, 17, "+ Nova ocorrência")
cols = [("ID", 3), ("Título", 10), ("Categoria", 38), ("Prior.", 58), ("Status", 68), ("Ações", 86)]
ax.add_patch(Rectangle((3, 31), 94, 4, fc="#E6EEF7", ec=BORDA))
for n, x in cols: ax.text(x + 1, 33, n, fontsize=8, weight="bold", va="center")
linhas = [("1", "Buraco na Rua das Flores", "Buraco na rua", "4", "ABERTA"),
          ("2", "Poste sem luz - Av. Brasil", "Iluminação", "3", "EM_ANALISE"),
          ("3", "Lixo acumulado na praça", "Lixo acumulado", "2", "EM_ANDAMENTO"),
          ("4", "Calçada quebrada", "Calçada", "5", "RESOLVIDA")]
for i, (a, b, c, d, e) in enumerate(linhas):
    y = 26 - i * 5
    ax.add_patch(Rectangle((3, y - 2), 94, 4.5, fc="white" if i % 2 else CZ, ec="#CCCCCC"))
    for (n, x), v in zip(cols, (a, b, c, d, e)): ax.text(x + 1, y, v, fontsize=7.5, va="center")
    ax.text(84, y, "Ver  Editar  Excluir", fontsize=6, va="center", color=AZ)
fig.savefig("proto_1_lista.png", dpi=150, bbox_inches="tight"); plt.close(fig)

# ---- Tela 2: nova ocorrência ----
fig, ax = tela("Registrar nova ocorrência", "https://bairro-em-dia/ocorrencias/nova")
campo(ax, 3, 40, 60, "Título *", "Ex.: Buraco na Rua das Flores")
campo(ax, 3, 28, 94, "Localização *", "Rua das Flores, 120")
campo(ax, 3, 14, 60, "Descrição *", "Descreva o problema...", h=8)
campo(ax, 68, 40, 29, "Categoria *", "Selecione  ▾")
campo(ax, 68, 28, 29, "Prioridade (1 a 5) *", "3  ▾")
campo(ax, 68, 14, 29, "Morador *", "Selecione  ▾", h=8)
botao(ax, 3, 3, 18, "Registrar"); botao(ax, 24, 3, 15, "Cancelar", cor="#777777")
fig.savefig("proto_2_nova.png", dpi=150, bbox_inches="tight"); plt.close(fig)

# ---- Tela 3: detalhe ----
fig, ax = tela("Ocorrência #1 - Buraco na Rua das Flores", "https://bairro-em-dia/ocorrencias/1")
ax.text(3, 44, "Local: Rua das Flores, 120", fontsize=9)
ax.text(3, 40, "Categoria: Buraco na rua      Morador: Maria da Silva", fontsize=9)
ax.text(3, 36, "Criada em: 18/11/2026 10:30", fontsize=9)
ax.text(3, 31, "Buraco grande em frente ao número 120, já causou acidente.", fontsize=9, color="#444")
ax.add_patch(Rectangle((3, 12), 94, 14, fc=CZ, ec=BORDA))
ax.text(5, 23.5, "Acompanhamento", fontsize=9, weight="bold")
ax.text(5, 19, "Status atual:  ● ABERTA   →  EM_ANALISE  →  EM_ANDAMENTO  →  RESOLVIDA", fontsize=8)
campo(ax, 5, 13.5, 20, "", "Novo status  ▾", h=4)
botao(ax, 27, 13.5, 14, "Atualizar status", h=4)
campo(ax, 50, 13.5, 14, "", "Prioridade 1-5", h=4)
botao(ax, 66, 13.5, 16, "Atualizar prioridade", h=4)
botao(ax, 3, 3, 12, "Editar"); botao(ax, 18, 3, 12, "Excluir", cor="#B00020")
fig.savefig("proto_3_detalhe.png", dpi=150, bbox_inches="tight"); plt.close(fig)

# ---- Tela 4: moradores e categorias ----
fig, ax = tela("Cadastros: moradores e categorias", "https://bairro-em-dia/cadastros")
ax.text(3, 46, "Moradores", fontsize=10, weight="bold")
campo(ax, 3, 36, 20, "Nome", "Maria da Silva", h=4); campo(ax, 25, 36, 22, "E-mail", "maria@example.com", h=4)
botao(ax, 3, 29, 14, "Salvar morador", h=4)
ax.text(52, 46, "Categorias", fontsize=10, weight="bold")
campo(ax, 52, 36, 18, "Nome", "Iluminação", h=4); campo(ax, 72, 36, 25, "Descrição", "Postes sem luz", h=4)
botao(ax, 52, 29, 15, "Salvar categoria", h=4)
for i, t in enumerate(["1  Maria da Silva  -  maria@example.com   [Editar] [Excluir]", "2  João Souza  -  joao@example.com   [Editar] [Excluir]"]):
    ax.text(3, 22 - i * 4, t, fontsize=7, color="#333")
for i, t in enumerate(["1  Buraco na rua   [Editar] [Excluir]", "2  Iluminação pública   [Editar] [Excluir]"]):
    ax.text(52, 22 - i * 4, t, fontsize=7, color="#333")
fig.savefig("proto_4_cadastros.png", dpi=150, bbox_inches="tight"); plt.close(fig)
print("ok")
