const API_BASE_URL = 'http://localhost:8080/api/clientes';
const API_CONTATOS_URL = 'http://localhost:8080/api';

// Variáveis Globais
let clienteIdSelecionado = null;
let clienteEditandoId = null;
let contatoEditandoId = null;
let listaClientesGlobal = [];
let listaContatosGlobal = [];

// ==========================================
// 1. VALIDAÇÃO DE DATA DE NASCIMENTO (RN08)
// ==========================================
function configurarValidacaoDeData() {
    const inputData = document.getElementById('dataNasc');
    const hoje = new Date();

    // Data Máxima: 18 anos atrás
    const maxDate = new Date(hoje.getFullYear() - 18, hoje.getMonth(), hoje.getDate());
    inputData.setAttribute('max', maxDate.toISOString().split('T')[0]);

    // Data Mínima: 120 anos atrás (Idade Realista)
    const minDate = new Date(hoje.getFullYear() - 120, hoje.getMonth(), hoje.getDate());
    inputData.setAttribute('min', minDate.toISOString().split('T')[0]);
}

// ==========================================
// 2. MÁSCARAS E VIACEP
// ==========================================
document.getElementById('cpf').addEventListener('input', function (e) {
    let valor = e.target.value.replace(/\D/g, '');
    if (valor.length > 11) valor = valor.slice(0, 11);

    // CORREÇÃO: Usando {1,2} para não quebrar a máscara ao apagar com backspace
    if (valor.length > 9) valor = valor.replace(/(\d{3})(\d{3})(\d{3})(\d{1,2})/, "$1.$2.$3-$4");
    else if (valor.length > 6) valor = valor.replace(/(\d{3})(\d{3})(\d{1,3})/, "$1.$2.$3");
    else if (valor.length > 3) valor = valor.replace(/(\d{3})(\d{1,3})/, "$1.$2");

    e.target.value = valor;
});

document.getElementById('cep').addEventListener('blur', async (event) => {
    let cep = event.target.value.replace(/\D/g, '');
    if (cep.length === 8) {
        try {
            const response = await fetch(`https://viacep.com.br/ws/${cep}/json/`);
            const data = await response.json();
            if (!data.erro) {
                document.getElementById('logradouro').value = data.logradouro;
                document.getElementById('bairro').value = data.bairro;
                document.getElementById('cidade').value = data.localidade;
                document.getElementById('estado').value = data.uf;
                document.getElementById('numero').focus();
            }
        } catch (error) { console.error('Erro ViaCEP:', error); }
    }
});

// Máscara de Contato
const tipoContatoSelect = document.getElementById('tipoContato');
const valorContatoInput = document.getElementById('valorContato');

tipoContatoSelect.addEventListener('change', function() {
    valorContatoInput.value = '';
    if (this.value === 'Telefone') {
        valorContatoInput.placeholder = '(00) 00000-0000';
        valorContatoInput.setAttribute('maxlength', '15');
    } else {
        valorContatoInput.placeholder = 'exemplo@email.com';
        valorContatoInput.removeAttribute('maxlength');
    }
});

valorContatoInput.addEventListener('input', function(e) {
    if (tipoContatoSelect.value === 'Telefone') {
        let valor = e.target.value.replace(/\D/g, "");
        if (valor.length > 10) valor = valor.replace(/^(\d{2})(\d{5})(\d{4}).*/, "($1) $2-$3");
        else if (valor.length > 6) valor = valor.replace(/^(\d{2})(\d{4})(\d{0,4}).*/, "($1) $2-$3");
        else if (valor.length > 2) valor = valor.replace(/^(\d{2})(\d{0,5})/, "($1) $2");
        else valor = valor.replace(/^(\d*)/, "($1");
        e.target.value = valor;
    }
});

// ==========================================
// 3. BUSCA INTELIGENTE (Nome ou CPF)
// ==========================================
async function buscarClientes() {
    let termoDeBusca = document.getElementById('buscaInput').value.trim();

    // Se o campo estiver vazio, carrega tudo de novo
    if (!termoDeBusca) {
        carregarClientes();
        return;
    }

    let apenasNumeros = termoDeBusca.replace(/\D/g, '');
    let url = '';

    // Lógica: Se tem exatos 11 números, é CPF. Se não, é Nome.
    if (apenasNumeros.length === 11) {
        url = `${API_BASE_URL}/cpf/${apenasNumeros}`;
    } else {
        url = `${API_BASE_URL}/nome?valor=${encodeURIComponent(termoDeBusca)}`;
    }

    try {
        const response = await fetch(url);
        if (!response.ok) {
            renderizarTabelaClientes([]); // Não encontrou, esvazia a tabela
            return;
        }

        const data = await response.json();
        // Garante que o resultado seja sempre um Array (lista)
        const resultados = Array.isArray(data) ? data : [data];
        renderizarTabelaClientes(resultados);
    } catch (error) {
        console.error('Erro na busca:', error);
    }
}

// ==========================================
// 4. CRUD DE CLIENTES
// ==========================================
async function carregarClientes() {
    try {
        const response = await fetch(API_BASE_URL);
        listaClientesGlobal = await response.json();
        document.getElementById('buscaInput').value = ''; // Limpa o campo de busca
        renderizarTabelaClientes(listaClientesGlobal);
    } catch (error) { console.error('Erro ao carregar clientes:', error); }
}

function renderizarTabelaClientes(lista) {
    const tbody = document.getElementById('listaClientes');
    tbody.innerHTML = '';

    lista.forEach(cliente => {
        const cpfFormatado = cliente.cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
        const cidade = cliente.endereco ? cliente.endereco.cidade : '-';

        const tr = document.createElement('tr');
        tr.innerHTML = `
            <td>${cliente.nome}</td>
            <td>${cpfFormatado}</td>
            <td>${cidade}</td>
            <td style="display: flex;">
                <button class="btn-contato" onclick="abrirModal(${cliente.id}, '${cliente.nome}')">Contatos</button>
                <button class="btn-edit" onclick="prepararEdicaoCliente(${cliente.id})">Editar</button>
                <button class="btn-delete" onclick="deletarCliente(${cliente.id})">Excluir</button>
            </td>
        `;
        tbody.appendChild(tr);
    });
}

function prepararEdicaoCliente(id) {
    const cliente = listaClientesGlobal.find(c => c.id === id);
    if (!cliente) return;

    clienteEditandoId = id;

    document.getElementById('nome').value = cliente.nome;
    document.getElementById('dataNasc').value = cliente.dataNasc;

    let cpf = cliente.cpf;
    cpf = cpf.replace(/(\d{3})(\d{3})(\d{3})(\d{2})/, "$1.$2.$3-$4");
    document.getElementById('cpf').value = cpf;

    if (cliente.endereco) {
        document.getElementById('cep').value = cliente.endereco.cep;
        document.getElementById('estado').value = cliente.endereco.estado;
        document.getElementById('cidade').value = cliente.endereco.cidade;
        document.getElementById('bairro').value = cliente.endereco.bairro;
        document.getElementById('logradouro').value = cliente.endereco.logradouro;
        document.getElementById('numero').value = cliente.endereco.numero;
        document.getElementById('complemento').value = cliente.endereco.complemento || '';
    } else {
        document.getElementById('cep').value = '';
        document.getElementById('estado').value = '';
        document.getElementById('cidade').value = '';
        document.getElementById('bairro').value = '';
        document.getElementById('logradouro').value = '';
        document.getElementById('numero').value = '';
        document.getElementById('complemento').value = '';
    }

    document.querySelector('#clienteForm button[type="submit"]').innerText = "Atualizar Cliente";
    window.scrollTo(0, 0);
}

document.getElementById('clienteForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const cliente = {
        nome: document.getElementById('nome').value,
        cpf: document.getElementById('cpf').value.replace(/\D/g, ''),
        dataNasc: document.getElementById('dataNasc').value,
        endereco: {
            cep: document.getElementById('cep').value.replace(/\D/g, ''),
            logradouro: document.getElementById('logradouro').value,
            numero: document.getElementById('numero').value,
            complemento: document.getElementById('complemento').value,
            bairro: document.getElementById('bairro').value,
            cidade: document.getElementById('cidade').value,
            estado: document.getElementById('estado').value
        }
    };

    if (!cliente.endereco.cep) delete cliente.endereco;

    const metodo = clienteEditandoId ? 'PUT' : 'POST';
    const url = clienteEditandoId ? `${API_BASE_URL}/${clienteEditandoId}` : API_BASE_URL;

    try {
        const response = await fetch(url, {
            method: metodo,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(cliente)
        });

       if (response.ok) {
           // Verifica se estamos editando ou criando
           if (clienteEditandoId) {
               alert('✏️ Cliente atualizado com sucesso!');
           } else {
               alert('✅ Cliente cadastrado com sucesso!');
           }

           document.getElementById('clienteForm').reset();
           clienteEditandoId = null;
           document.querySelector('#clienteForm button[type="submit"]').innerText = "Salvar Cliente";
           carregarClientes();
        } else {
            // CORREÇÃO: Tratamento de mensagens de erro elegantes para Clientes
            const errorData = await response.json();
            let mensagemBonita = "⚠️ Verifique os seguintes itens:\n\n";

            if (Array.isArray(errorData)) {
                errorData.forEach(erro => {
                    mensagemBonita += `• ${erro.mensagem || erro.message || "Erro de validação"}\n`;
                });
            } else if (errorData.message) {
                mensagemBonita += `• ${errorData.message}\n`;
            } else {
                mensagemBonita += "• Ocorreu um erro inesperado no servidor.";
            }

            alert(mensagemBonita);
        }
    } catch (error) {
            // Agora ele vai imprimir o erro real no F12 para você investigar!
            console.error("Erro capturado no fetch:", error);

            alert('Erro no servidor! O banco de dados recusou a operação (Possível CPF duplicado ou erro interno).');
        }
    });

async function deletarCliente(id) {
    if (confirm('Excluir este cliente e todos os seus contatos?')) {
        await fetch(`${API_BASE_URL}/${id}`, { method: 'DELETE' });
        if(clienteEditandoId === id) {
            document.getElementById('clienteForm').reset();
            clienteEditandoId = null;
            document.querySelector('#clienteForm button[type="submit"]').innerText = "Salvar Cliente";
        }
        carregarClientes();
    }
}

// ==========================================
// 5. CRUD DE CONTATOS (MODAL)
// ==========================================
function abrirModal(clienteId, clienteNome) {
    clienteIdSelecionado = clienteId;
    document.getElementById('modalNomeCliente').innerText = `Contatos de ${clienteNome}`;
    document.getElementById('modalContatos').style.display = 'flex';
    carregarContatosDoCliente();
}

function fecharModal() {
    document.getElementById('modalContatos').style.display = 'none';
    clienteIdSelecionado = null;
    contatoEditandoId = null;
    document.getElementById('contatoForm').reset();
    document.querySelector('#contatoForm button[type="submit"]').innerText = "Adicionar Contato";
}

async function carregarContatosDoCliente() {
    try {
        const response = await fetch(`${API_CONTATOS_URL}/clientes/${clienteIdSelecionado}/contatos`);
        listaContatosGlobal = await response.json();

        const tbody = document.getElementById('listaContatosModal');
        tbody.innerHTML = '';

        listaContatosGlobal.forEach(contato => {
            const tr = document.createElement('tr');
            tr.innerHTML = `
                <td>${contato.tipo}</td>
                <td>${contato.valor}</td>
                <td>${contato.observacao || '-'}</td>
                <td style="display: flex;">
                    <button class="btn-edit" onclick="prepararEdicaoContato(${contato.id})">Editar</button>
                    <button class="btn-delete" onclick="deletarContato(${contato.id})">Excluir</button>
                </td>
            `;
            tbody.appendChild(tr);
        });
    } catch (error) { console.error('Erro ao carregar contatos', error); }
}

function prepararEdicaoContato(id) {
    const contato = listaContatosGlobal.find(c => c.id === id);
    if (!contato) return;

    contatoEditandoId = id;
    document.getElementById('tipoContato').value = contato.tipo;
    document.getElementById('valorContato').value = contato.valor;
    document.getElementById('obsContato').value = contato.observacao || '';

    document.getElementById('tipoContato').dispatchEvent(new Event('change'));
    document.getElementById('valorContato').value = contato.valor;

    document.querySelector('#contatoForm button[type="submit"]').innerText = "Atualizar Contato";
}

document.getElementById('contatoForm').addEventListener('submit', async (e) => {
    e.preventDefault();
    const novoContato = {
        tipo: document.getElementById('tipoContato').value,
        valor: document.getElementById('valorContato').value,
        observacao: document.getElementById('obsContato').value
    };

    const metodo = contatoEditandoId ? 'PUT' : 'POST';
    const url = contatoEditandoId
                ? `${API_CONTATOS_URL}/contatos/${contatoEditandoId}`
                : `${API_CONTATOS_URL}/clientes/${clienteIdSelecionado}/contatos`;

    try {
        const response = await fetch(url, {
            method: metodo,
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(novoContato)
        });

        if (response.ok) {
            document.getElementById('contatoForm').reset();
            contatoEditandoId = null;
            document.querySelector('#contatoForm button[type="submit"]').innerText = "Adicionar Contato";
            carregarContatosDoCliente();
        } else {
            // CORREÇÃO: Tratamento de mensagens de erro elegantes para Contatos
            const errorData = await response.json();
            let mensagemBonita = "⚠️ Verifique os seguintes itens:\n\n";

            if (Array.isArray(errorData)) {
                errorData.forEach(erro => {
                    mensagemBonita += `• ${erro.mensagem || erro.message || "Erro de validação"}\n`;
                });
            } else if (errorData.message) {
                mensagemBonita += `• ${errorData.message}\n`;
            } else {
                mensagemBonita += "• Ocorreu um erro inesperado no servidor.";
            }

            alert(mensagemBonita);
        }
    } catch (error) { console.error('Erro', error); }
});

async function deletarContato(id) {
    if (confirm('Excluir este contato?')) {
        await fetch(`${API_CONTATOS_URL}/contatos/${id}`, { method: 'DELETE' });
        carregarContatosDoCliente();
    }
}

// INICIALIZAÇÃO
configurarValidacaoDeData();
carregarClientes();