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
            <a href="ordini-live" class="logo-text">
                <i class="fa-solid fa-utensils"></i> WebDelivery
            </a>
        </div>
        <nav class="sidebar-nav">
            <a href="ordini-live" class="nav-item active"><i class="fa-solid fa-fire-burner"></i> Ordini Live</a>
            <a href="storico-ordini" class="nav-item"><i class="fa-solid fa-clock-rotate-left"></i> Storico Ordini</a>
        </nav>
        <div class="sidebar-footer">
            <a href="../home" class="nav-item logout-btn"><i class="fa-solid fa-arrow-right-from-bracket"></i> Esci</a>
        </div>
    </aside>

    <main class="admin-content">
        <header class="admin-header">
            <div>
                <h1>
                    <a href="ordini-live" style="color: #4a5568; text-decoration: none; margin-right: 15px;">
                        <i class="fa-solid fa-arrow-left"></i>
                    </a> 
                    Dettaglio Ordine #${ordine.id}
                </h1>
            </div>
            <div class="user-badge">Operatore: ${(utenteLoggato.nomeCompleto)!''}</div>
        </header>

        <section class="admin-section">
            <div style="display: flex; justify-content: space-between; margin-bottom: 30px; padding-bottom: 20px; border-bottom: 1px solid #e2e8f0;">
                <div>
                    <h3 style="color: #4a5568; margin-top: 0;"><i class="fa-solid fa-user"></i> Dati Cliente</h3>
                    <p><strong>Nome:</strong> ${ordine.utente.nomeCompleto}</p>
                    <p><strong>Orario Richiesto:</strong> ${ordine.orarioConsegnaRichiesto}</p>
                    <p><strong>Tempo Preparazione:</strong> ${ordine.tempoStimatoConsegna} min</p>
                </div>
                <div style="text-align: right;">
                    <h3 style="color: #4a5568; margin-top: 0;"><i class="fa-solid fa-info-circle"></i> Info Ordine</h3>
                    <p><strong>Stato:</strong> <span class="status-badge status-${ordine.stato?replace(' ', '-')}">${ordine.stato?upper_case}</span></p>
                    
                    <p style="margin-top: 10px; font-size: 0.95rem; color: #718096;">
                        <i class="fa-solid fa-user-gear"></i> <strong>Gestito da:</strong> 
                        <#if ordine.storicoStati?has_content>
                            ${ordine.storicoStati?sort_by("dataOraModifica")?reverse[0].personale.nomeCompleto}
                        </#if>
                    </p>
                    
                    <p style="font-size: 1.2rem; margin-top: 15px;"><strong>Totale:</strong> € ${ordine.prezzoTotale}</p>
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
                                <td>
                                    <div style="font-weight: 600; color: #2d3748;">${dettaglio.prodotto.nome}</div>
                                    
                                    <div style="font-size: 0.85rem; color: #4a5568; margin-top: 5px;">
                                        <i class="fa-solid fa-leaf" style="color: #48bb78;"></i> 
                                        <strong>Ingredienti:</strong> 
                                        ${(dettaglio.prodotto.ingredienti)!'Nessun ingrediente specificato.'}
                                    </div>
                                    
                                    <div style="font-size: 0.85rem; color: #718096; margin-top: 3px; font-style: italic;">
                                        <i class="fa-solid fa-fire-burner" style="color: #e53e3e;"></i> 
                                        <strong>Preparazione:</strong> 
                                        ${(dettaglio.prodotto.ricetta)!'Nessuna istruzione di cottura.'}
                                    </div>
                                </td>
                                <td style="vertical-align: top; padding-top: 15px;">x ${dettaglio.quantita}</td>
                                <td style="vertical-align: top; padding-top: 15px;">€ ${dettaglio.prodotto.prezzo}</td>
                                <td style="vertical-align: top; padding-top: 15px; font-weight: bold;">€ ${dettaglio.quantita * dettaglio.prodotto.prezzo}</td>
                            </tr>
                        </#list>
                    <#else>
                        <tr><td colspan="4" style="text-align: center;">Nessun prodotto trovato.</td></tr>
                    </#if>
                </tbody>
            </table>
        </section>

        <section class="admin-section" style="margin-top: 30px; background-color: #f8fafc; border: 1px solid #e2e8f0;">
            <h3 style="color: #4a5568;"><i class="fa-solid fa-clock-rotate-left"></i> Cronologia Modifiche Stato</h3>
            
            <table class="admin-table" style="background-color: white;">
                <thead>
                    <tr>
                        <th>Data e Ora</th>
                        <th>Nuovo Stato</th>
                        <th>Modificato da</th>
                    </tr>
                </thead>
                <tbody>
                    <#if ordine.storicoStati?has_content>
                        <#list ordine.storicoStati?sort_by("dataOraModifica")?reverse as storico>
                            <tr>
                                <td>${storico.dataOraModifica}</td>
                                <td><span class="status-badge status-${storico.stato?replace(' ', '-')}">${storico.stato?upper_case}</span></td>
                                <td>
                                    <i class="fa-solid fa-user-gear" style="color: #718096; margin-right: 5px;"></i>
                                    <strong>${storico.personale.nomeCompleto}</strong>
                                </td>
                            </tr>
                        </#list>
                    <#else>
                        <tr><td colspan="3" style="text-align: center; color: #718096;">Nessuna modifica registrata. L'ordine è appena stato inserito.</td></tr>
                    </#if>
                </tbody>
            </table>
        </section>
    </main>

</body>
</html>
