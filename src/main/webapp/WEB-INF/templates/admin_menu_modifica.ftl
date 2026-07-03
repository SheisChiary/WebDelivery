<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Modifica Prodotto - ${prodotto.nome}</title>
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.4.0/css/all.min.css">
    <link rel="stylesheet" type="text/css" href="../css/style.css">
</head>
<body class="admin-body">

       <aside class="dash-sidebar">
            <div class="dash-logo" style="margin-top: 10px;">
                <i class="fa-solid fa-utensils"></i> WebDelivery
            </div>
            
            <ul class="dash-nav">
                <li><a href="ordini"><i class="fa-solid fa-receipt"></i> Ordini</a></li>
                <li><a href="menu" class="active"><i class="fa-solid fa-burger"></i> Gestione Menù</a></li>
                <li><a href="staff"><i class="fa-solid fa-users"></i> Gestione Staff</a></li>
                <li><a href="statistiche"><i class="fa-solid fa-chart-line"></i> Statistiche</a></li>
                
                <li style="margin-top: auto;">
                    <a href="../logout" class="danger"><i class="fa-solid fa-arrow-right-from-bracket"></i> Esci</a>
                </li>
            </ul>
        </aside>

    <main class="admin-content">
        <header class="admin-header">
            <h1>Modifica: ${prodotto.nome}</h1>
            <a href="menu" class="btn btn-outline"><i class="fa-solid fa-arrow-left"></i> Torna al Menù</a>
        </header>

        <section class="admin-section" style="margin-bottom: 40px;">
            <h2>Dati Base del Prodotto</h2>
            <form action="menu-modifica" method="POST" style="margin-top: 20px;">
                <input type="hidden" name="id" value="${prodotto.id}">
                <input type="hidden" name="subAction" value="update_prodotto">

                <div class="form-grid">
                    <div class="form-group">
                        <label>Nome Piatto</label>
                        <input type="text" name="nome" value="${prodotto.nome}" required>
                    </div>
                    <div class="form-group">
                        <label>Prezzo (€)</label>
                        <input type="number" step="0.01" name="prezzo" value="${prodotto.prezzo}" required>
                    </div>
                </div>
                <div class="form-grid">
                    <div class="form-group">
                        <label>Categoria</label>
                        <input type="text" name="categoria" value="${prodotto.categoria}" required>
                    </div>
                    <div class="form-group">
                        <label>Tempo Prep. (min)</label>
                        <input type="number" name="tempo_preparazione" value="${prodotto.tempoPreparazione}" required>
                    </div>
                </div>
                <div class="form-grid">
                    <div class="form-group">
                        <label>URL Immagine</label>
                        <input type="text" name="immagine" value="${prodotto.immagine!''}">
                    </div>
                    <div class="form-group">
                        <label>Badge</label>
                        <input type="text" name="badge" value="${prodotto.badge!''}">
                    </div>
                </div>
                <div class="form-group">
                    <label>Descrizione Ingredienti</label>
                    <input type="text" name="descrizione" value="${prodotto.descrizione!''}" required>
                </div>
                <button type="submit" class="btn-green">Salva Modifiche Piatto</button>
            </form>
        </section>

        <section class="admin-section">
            <h2>Gestione Varianti e Modifiche (Ingredienti Extra / Opzioni)</h2>
            
            <form action="menu-modifica" method="POST" style="background:#f8fafc; padding:20px; border-radius:8px; margin-top:20px; margin-bottom:20px;">
                <input type="hidden" name="id" value="${prodotto.id}">
                <input type="hidden" name="subAction" value="add_caratteristica">
                
                <h3 style="font-size:1.1rem; color:#116C4A; margin-bottom:15px;">Aggiungi Opzione di Personalizzazione</h3>
                <div class="form-grid">
                    <div class="form-group">
                        <label>Nome Opzione (es: "Doppio Mozzarella", "Coca Zero")</label>
                        <input type="text" name="caratt_nome" required>
                    </div>
                    <div class="form-group">
                        <label>Differenza Prezzo (€) (es: 1.50 o -0.50 o 0.00)</label>
                        <input type="number" step="0.01" name="caratt_prezzo" value="0.00" required>
                    </div>
                </div>
                <div class="form-grid">
                    <div class="form-group">
                        <label>Gruppo di Esclusione (Opzionale)</label>
                        <select name="id_gruppo">
                            <option value="">Nessuno (Ingrediente Extra Singolo)</option>
                            <#list gruppi as g>
                                <option value="${g.id}">${g.nomeGruppo}</option>
                            </#list>
                        </select>
                    </div>
                    <div class="form-group" style="justify-content: center;">
                        <label style="display:flex; align-items:center; gap:10px; margin-top:25px; cursor:pointer;">
                            <input type="checkbox" name="caratt_default" style="width:20px; height:20px;"> Imposta come Selezionato di Default
                        </label>
                    </div>
                </div>
                <button type="submit" class="btn-action-state" style="width:auto; padding:10px 25px;">Aggiungi Modifica</button>
            </form>

            <table class="admin-table">
                <thead>
                    <tr>
                        <th>Nome Opzione</th>
                        <th>Prezzo Extra</th>
                        <th>Gruppo di Appartenenza</th>
                        <th style="text-align: center;">Stato Default</th>
                        <th style="text-align: center;">Azione</th>
                    </tr>
                </thead>
                <tbody>
                    <#if caratteristiche?has_content>
                        <#list caratteristiche as c>
                            <tr>
                                <td><strong>${c.nome}</strong></td>
                                <td><#if c.differenzaPrezzo &gt; 0>+ € ${c.differenzaPrezzo?string("0.00")}<#else>€ ${c.differenzaPrezzo?string("0.00")}</#if></td>
                                <td>${(c.gruppo.nomeGruppo)!'Extra / Singolo'}</td>
                                <td style="text-align: center;">
                                    <#if c.isDefault>
                                        <span style="color:#116C4A; font-weight:bold;"><i class="fa-solid fa-circle-check"></i> Attivo</span>
                                    <#else>
                                        <span style="color:#a0aec0;">-</span>
                                    </#if>
                                </td>
                                <td style="text-align: center;">
                                    <form action="menu-modifica" method="POST" onsubmit="return confirm('Rimuovere questa opzione?');">
                                        <input type="hidden" name="id" value="${prodotto.id}">
                                        <input type="hidden" name="subAction" value="delete_caratteristica">
                                        <input type="hidden" name="id_caratteristica" value="${c.id}">
                                        <button type="submit" style="background:none; border:none; padding:0;" class="action-icon delete">
                                            <i class="fa-solid fa-trash"></i>
                                        </button>
                                    </form>
                                </td>
                            </tr>
                        </#list>
                    <#else>
                        <tr><td colspan="5" style="text-align: center; color:#718096;">Nessuna modifica personalizzata impostata per questo piatto.</td></tr>
                    </#if>
                </tbody>
            </table>
        </section>
    </main>

</body>
</html>