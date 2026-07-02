<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Dettagli Ordine #${ordine.id}</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" type="text/css" href="../css/style.css">
</head>
<body class="admin-body">

    <aside class="sidebar">
        <div class="sidebar-logo">
            <img src="../img/logo_admin.png" alt="WebDelivery Logo">
        </div>
        <nav class="sidebar-nav">
            <a href="ordini" class="nav-item active"><i class="fa-solid fa-receipt"></i> Ordini</a>
            <a href="menu" class="nav-item"><i class="fa-solid fa-burger"></i> Gestione Menù</a>
            <a href="staff" class="nav-item"><i class="fa-solid fa-users"></i> Gestione Staff</a>
            <a href="statistiche" class="nav-item"><i class="fa-solid fa-chart-line"></i> Statistiche</a>
        </nav>
        <div class="sidebar-footer">
            <a href="../logout" class="nav-item logout-btn"><i class="fa-solid fa-arrow-right-from-bracket"></i> Esci</a>
        </div>
    </aside>

    <main class="admin-content">
        <header class="admin-header">
            <div>
                <h1>
                    <a href="ordini" style="color: #4a5568; text-decoration: none; margin-right: 15px;">
                        <i class="fa-solid fa-arrow-left"></i>
                    </a> 
                    Dettaglio Ordine #${ordine.id}
                </h1>
            </div>
            <div class="user-badge">Operatore: ${utenteLoggato.nomeCompleto}</div>
        </header>

        <section class="admin-section">
            <div style="display: flex; justify-content: space-between; margin-bottom: 30px; padding-bottom: 20px; border-bottom: 1px solid #e2e8f0;">
                <div>
                    <h3 style="color: #4a5568; margin-top: 0;"><i class="fa-solid fa-user"></i> Dati Cliente</h3>
                    <p><strong>Nome:</strong> ${ordine.utente.nomeCompleto}</p>
                    <p><strong>Orario Richiesto:</strong> ${ordine.orarioConsegnaRichiesto}</p>
                    <p><strong>Tempo Preparazione Stimato:</strong> ${ordine.tempoStimatoConsegna} min</p>
                </div>
                <div style="text-align: right;">
                    <h3 style="color: #4a5568; margin-top: 0;"><i class="fa-solid fa-info-circle"></i> Info Ordine</h3>
                    <p><strong>Stato:</strong> <span class="status-badge status-${ordine.stato?replace(' ', '-')}">${ordine.stato?upper_case}</span></p>
                    <p style="font-size: 1.2rem;"><strong>Totale:</strong> € ${ordine.prezzoTotale}</p>
                </div>
            </div>

            <h3 style="color: #4a5568;"><i class="fa-solid fa-list"></i> Prodotti Ordinati</h3>
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>Prodotto</th>
                        <th>Quantità</th>
                        <th>Prezzo Unitario</th>
                        <th>Subtotale</th>
                    </tr>
                </thead>
                <tbody>
                    <#if ordine.dettagli?has_content>
                        <#list ordine.dettagli as dettaglio>
                            <tr>
                                <td>${dettaglio.prodotto.nome}</td>
                                <td>x ${dettaglio.quantita}</td>
                                <td>€ ${dettaglio.prodotto.prezzo}</td>
                                <td>€ ${dettaglio.quantita * dettaglio.prodotto.prezzo}</td>
                            </tr>
                        </#list>
                    <#else>
                        <tr><td colspan="4" style="text-align: center;">Nessun prodotto trovato.</td></tr>
                    </#if>
                </tbody>
            </table>
        </section>
    </main>

</body>
</html>
