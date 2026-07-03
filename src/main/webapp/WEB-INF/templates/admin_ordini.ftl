
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Dashboard Admin - Ordini</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" type="text/css" href="../css/style.css">
</head>
<body class="admin-body">

   <aside class="dash-sidebar">
            <div class="dash-logo" style="margin-top: 10px;">
                <i class="fa-solid fa-utensils"></i> WebDelivery
            </div>
            
            <ul class="dash-nav">
                <li><a href="ordini" class="active"><i class="fa-solid fa-receipt"></i> Ordini</a></li>
                <li><a href="menu"><i class="fa-solid fa-burger"></i> Gestione Menù</a></li>
                <li><a href="staff"><i class="fa-solid fa-users"></i> Gestione Staff</a></li>
                <li><a href="statistiche"><i class="fa-solid fa-chart-line"></i> Statistiche</a></li>
                
                <li style="margin-top: auto;">
                    <a href="../logout" class="danger"><i class="fa-solid fa-arrow-right-from-bracket"></i> Esci</a>
                </li>
            </ul>
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
                                
                                <td style="font-weight: 500; color: #4a5568;">
                                    <i class="fa-regular fa-calendar" style="color: #718096; margin-right: 5px;"></i>
                                    Prog. per le ${ordine.orarioConsegnaRichiesto?string?substring(11, 16)}
                                </td>
                                
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
                        <th>Orario Consegna</th>
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
                                
                                <td>
                                    <#if ordine.storicoStati?has_content>
                                        <#assign dataConsegna = ordine.storicoStati?sort_by("dataOraModifica")?reverse[0].dataOraModifica>
                                        <i class="fa-solid fa-circle-check" style="color: #48bb78; margin-right: 5px;"></i>
                                        <span style="color: #4a5568; font-weight: 500;">
                                            Il ${dataConsegna?string?substring(0, 10)} alle ${dataConsegna?string?substring(11, 16)}
                                        </span>
                                    </#if>
                                </td>
                                
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
