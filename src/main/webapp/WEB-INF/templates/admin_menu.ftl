<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Dashboard Admin - Gestione Menù</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" type="text/css" href="../css/style.css">
</head>
<body class="admin-body">

    <aside class="sidebar">
        <div class="sidebar-logo">
           <a href="ordini" class="logo-text">
           <i class="fa-solid fa-utensils"></i> WebDelivery
           </a>
        </div>
        <nav class="sidebar-nav">
            <a href="ordini" class="nav-item"><i class="fa-solid fa-receipt"></i> Ordini</a>
            <a href="menu" class="nav-item active"><i class="fa-solid fa-burger"></i> Gestione Menù</a>
            <a href="staff" class="nav-item"><i class="fa-solid fa-users"></i> Gestione Staff</a>
            <a href="statistiche" class="nav-item"><i class="fa-solid fa-chart-line"></i> Statistiche</a>
        </nav>
        <div class="sidebar-footer">
            <a href="../logout" class="nav-item logout-btn"><i class="fa-solid fa-arrow-right-from-bracket"></i> Esci</a>
        </div>
    </aside>

    <main class="admin-content">
        <header class="admin-header">
            <h1>Gestione Menù</h1>
            <div class="user-badge">Proprietario: ${utenteLoggato.nomeCompleto}</div>
        </header>

        <section class="admin-section" style="margin-bottom: 40px;">
            <h2><i class="fa-solid fa-plus" style="color: #116C4A; margin-right: 10px;"></i> Aggiungi Nuovo Prodotto</h2>
            <form action="menu" method="POST" style="margin-top: 20px;">
                <div class="form-grid">
                    <div class="form-group">
                        <label>Nome Piatto</label>
                        <input type="text" name="nome" required>
                    </div>
                    <div class="form-group">
                        <label>Prezzo (€)</label>
                        <input type="number" step="0.01" name="prezzo" required>
                    </div>
                </div>
                <div class="form-grid">
                    <div class="form-group">
                        <label>Categoria</label>
                        <input type="text" name="categoria" placeholder="Pizze, Burger, Bevande..." required>
                    </div>
                    <div class="form-group">
                        <label>Tempo Prep. (minuti)</label>
                        <input type="number" name="tempo_preparazione" required>
                    </div>
                </div>
                <div class="form-grid">
                    <div class="form-group">
                        <label>URL Immagine</label>
                        <input type="text" name="immagine">
                    </div>
                    <div class="form-group">
                        <label>Badge (Opzionale)</label>
                        <input type="text" name="badge" placeholder="Novità, Piccante...">
                    </div>
                </div>
                <div class="form-group">
                    <label>Descrizione Ingredienti</label>
                    <input type="text" name="descrizione" required>
                </div>
                <button type="submit" class="btn-green" style="margin-top: 10px;">Inserisci nel Menù</button>
            </form>
        </section>

        <section class="admin-section">
            <h2><i class="fa-solid fa-book-open" style="color: #116C4A; margin-right: 10px;"></i> Piatti nel Catalogo</h2>
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>Immagine</th>
                        <th>Nome</th>
                        <th>Categoria</th>
                        <th>Prezzo</th>
                        <th>Tempo Prep.</th>
                        <th style="text-align: center;">Azioni</th>
                    </tr>
                </thead>
                <tbody>
                    <#if menu?has_content>
                        <#list menu as prodotto>
                            <tr>
                                <td><img src="${prodotto.immagine!''}" style="width: 50px; height: 50px; object-fit: cover; border-radius: 8px;"></td>
                                <td><strong>${prodotto.nome}</strong><br><small style="color:#718096;">${prodotto.descrizione!''}</small></td>
                                <td>${prodotto.categoria}</td>
                                <td>€ ${prodotto.prezzo?string("0.00")}</td>
                                <td>${prodotto.tempoPreparazione} min</td>
                                <td style="text-align: center;">
                                    <a href="menu-modifica?id=${prodotto.id}" class="action-icon edit" title="Modifica Piatto e Caratteristiche">
                                        <i class="fa-solid fa-pen"></i>
                                    </a>
                                    <form action="menu-elimina" method="POST" style="display:inline;" onsubmit=\"return confirm('Rimuovere ${prodotto.nome} dal menù?');\">
                                        <input type="hidden" name="id" value="${prodotto.id}">
                                        <button type="submit" style="background:none; border:none; padding:0;" class="action-icon delete" title="Elimina">
                                            <i class="fa-solid fa-trash"></i>
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </#list>
                    <#else>
                        <tr><td colspan="6" style="text-align: center;">Il menù è vuoto.</td></tr>
                    </#if>
                </tbody>
            </table>
        </section>
    </main>

</body>
</html>