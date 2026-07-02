<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Dashboard Admin - Ordini</title>
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
            <a href="ordini" class="nav-item active"><i class="fa-solid fa-receipt"></i> Ordini</a>
            <a href="menu" class="nav-item"><i class="fa-solid fa-burger"></i> Gestione Menù</a>
            <a href="staff" class="nav-item"><i class="fa-solid fa-users"></i> Gestione Staff</a>
            <a href="statistiche" class="nav-item"><i class="fa-solid fa-chart-line"></i> Statistiche</a>
        </nav>
        
        <div class="sidebar-footer">
            <a href="../home" class="nav-item logout-btn"><i class="fa-solid fa-arrow-right-from-bracket"></i> Esci</a>
        </div>
    </aside>

    <main class="admin-content">
        <header class="admin-header">
            <h1>Gestione Ordini</h1>
            <div class="user-badge">Operatore: ${utenteLoggato.nomeCompleto}</div>
        </header>

        <section class="admin-section">
            <h2><i class="fa-solid fa-fire"></i> Ordini in Corso</h2>
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Cliente</th>
                        <th>Orario Richiesto</th>
                        <th>Stato</th>
                        <th>Totale</th>
                        <th>Azioni / Dettagli</th>
                    </tr>
                </thead>
                <tbody>
                    <#if ordiniInCorso?has_content>
                        <#list ordiniInCorso as ordine>
                            <tr>
                                <td>#${ordine.id}</td>
                                <td>${ordine.utente.nomeCompleto}</td>
                                <td>${ordine.orarioConsegnaRichiesto}</td>
                                <td><span class="status-badge status-${ordine.stato?replace(' ', '-')}">${ordine.stato?upper_case}</span></td>
                                <td>€ ${ordine.prezzoTotale}</td>
                                <td>
                                    <form action="dettaglio-ordine" method="GET" style="margin:0;">
                                        <input type="hidden" name="id" value="${ordine.id}">
                                        <button type="submit" class="btn btn-outline btn-sm">Vedi Dettagli</button>
                                    </form>
                                </td>
                            </tr>
                        </#list>
                    <#else>
                        <tr><td colspan="6" style="text-align: center;">Nessun ordine in corso al momento.</td></tr>
                    </#if>
                </tbody>
            </table>
        </section>

        <section class="admin-section" style="margin-top: 40px;">
            <h2><i class="fa-solid fa-check-double"></i> Storico Ordini</h2>
            <table class="admin-table">
                <thead>
                    <tr>
                        <th>ID</th>
                        <th>Cliente</th>
                        <th>Data Creazione</th>
                        <th>Stato</th>
                        <th>Totale</th>
                    </tr>
                </thead>
                <tbody>
                    <#if ordiniPassati?has_content>
                        <#list ordiniPassati as ordine>
                            <tr class="row-disabled">
                                <td>#${ordine.id}</td>
                                <td>${ordine.utente.nomeCompleto}</td>
                                <td>${ordine.dataCreazione}</td>
                                <td><span class="status-badge status-${ordine.stato}">${ordine.stato?upper_case}</span></td>
                                <td>€ ${ordine.prezzoTotale}</td>
                            </tr>
                        </#list>
                    <#else>
                        <tr><td colspan="5" style="text-align: center;">Nessun ordine nello storico.</td></tr>
                    </#if>
                </tbody>
            </table>
        </section>
    </main>

</body>
</html>
