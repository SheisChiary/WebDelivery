<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Dashboard Admin - Statistiche</title>
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
            <a href="menu" class="nav-item"><i class="fa-solid fa-burger"></i> Gestione Menù</a>
            <a href="staff" class="nav-item"><i class="fa-solid fa-users"></i> Gestione Staff</a>
            <a href="statistiche" class="nav-item active"><i class="fa-solid fa-chart-line"></i> Statistiche</a>
        </nav>
        <div class="sidebar-footer">
            <a href="../home" class="nav-item logout-btn"><i class="fa-solid fa-arrow-right-from-bracket"></i> Esci</a>
        </div>
    </aside>

    <main class="admin-content">
        <header class="admin-header">
            <h1>Statistiche e Performance</h1>
            <div class="user-badge">Proprietario: ${utenteLoggato.nomeCompleto}</div>
        </header>

        <form action="statistiche" method="GET" class="date-filter-form">
            <label for="data_rif" style="font-weight: 600; color: #4a5568;"><i class="fa-regular fa-calendar"></i> Seleziona Data di Riferimento:</label>
            <input type="date" id="data_rif" name="data_rif" value="${dataSelezionata}">
            <button type="submit" class="btn-green" style="padding: 10px 20px;"><i class="fa-solid fa-filter"></i> Aggiorna</button>
        </form>

        <div class="stats-grid">
            <div class="stat-card">
                <span class="stat-title">Incasso del Giorno</span>
                <span class="stat-value">€ ${incassoGiornaliero?string("0.00")}</span>
                <i class="fa-solid fa-coins stat-icon"></i>
            </div>
            <div class="stat-card">
                <span class="stat-title">Incasso del Mese</span>
                <span class="stat-value">€ ${incassoMensile?string("0.00")}</span>
                <i class="fa-solid fa-vault stat-icon"></i>
            </div>
            <div class="stat-card info">
                <span class="stat-title">Scontrino Medio Globale</span>
                <span class="stat-value">€ ${scontrinoMedio?string("0.00")}</span>
                <i class="fa-solid fa-receipt stat-icon"></i>
            </div>
            <div class="stat-card warning">
                <span class="stat-title">Tasso di Annullamento</span>
                <span class="stat-value">${tassoAnnullamento?string("0.0")}%</span>
                <i class="fa-solid fa-ban stat-icon"></i>
            </div>
        </div>

        <div class="tables-grid">
            <section class="admin-section" style="margin-top: 0;">
                <h2 style="color: #276749;"><i class="fa-solid fa-arrow-trend-up"></i> Top 5 Prodotti (Più venduti)</h2>
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th>Prodotto</th>
                            <th style="text-align: right;">Quantità Vendute</th>
                        </tr>
                    </thead>
                    <tbody>
                        <#if topProdotti?has_content>
                            <#list topProdotti as row>
                                <tr>
                                    <td style="font-weight: 600;">${row[0]}</td>
                                    <td style="text-align: right; color: #4a5568;">${row[1]} pz.</td>
                                </tr>
                            </#list>
                        <#else>
                            <tr><td colspan="2" style="text-align: center;">Nessun dato di vendita.</td></tr>
                        </#if>
                    </tbody>
                </table>
            </section>

            <section class="admin-section" style="margin-top: 0;">
                <h2 style="color: #c53030;"><i class="fa-solid fa-arrow-trend-down"></i> Flop 5 Prodotti (Meno venduti)</h2>
                <table class="admin-table">
                    <thead>
                        <tr>
                            <th>Prodotto</th>
                            <th style="text-align: right;">Quantità Vendute</th>
                        </tr>
                    </thead>
                    <tbody>
                        <#if flopProdotti?has_content>
                            <#list flopProdotti as row>
                                <tr>
                                    <td style="font-weight: 600;">${row[0]}</td>
                                    <td style="text-align: right; color: #4a5568;">${row[1]} pz.</td>
                                </tr>
                            </#list>
                        <#else>
                            <tr><td colspan="2" style="text-align: center;">Nessun dato di vendita.</td></tr>
                        </#if>
                    </tbody>
                </table>
            </section>
        </div>

    </main>

</body>
</html>
