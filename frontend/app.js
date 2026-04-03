const API = 'http://localhost:8080/api';

// Cargar productos al iniciar
async function loadProducts() {
  const res = await fetch(`${API}/products`);
  const products = await res.json();
  renderTable(products);
  checkLowStock(products);
}

function renderTable(products) {
  const tbody = document.getElementById('products-body');
  tbody.innerHTML = products.map(p => `
    <tr class="${p.stock <= p.minStock ? 'low-stock-row' : ''}">
      <td>${p.name}</td>
      <td>${p.category?.name || '-'}</td>
      <td>$${p.price}</td>
      <td>${p.stock}</td>
      <td><span class="badge ${p.stock <= p.minStock ? 'badge-danger' : 'badge-ok'}">
        ${p.stock <= p.minStock ? '⚠ Bajo' : '✓ OK'}
      </span></td>
      <td>
        <button onclick="editProduct(${p.id})">✏</button>
        <button class="danger" onclick="deleteProduct(${p.id})">🗑</button>
      </td>
    </tr>`).join('');
}

function checkLowStock(products) {
  const low = products.filter(p => p.stock <= p.minStock);
  const alert = document.getElementById('low-stock-alert');
  if (low.length > 0) {
    alert.textContent = `⚠️ ${low.length} producto(s) con stock bajo`;
    alert.classList.remove('hidden');
  }
}

async function saveProduct() {
  const id = document.getElementById('product-id').value;
  const body = {
    name: document.getElementById('product-name').value,
    description: document.getElementById('product-desc').value,
    price: parseFloat(document.getElementById('product-price').value),
    stock: parseInt(document.getElementById('product-stock').value),
    minStock: parseInt(document.getElementById('product-minstock').value),
    category: { id: parseInt(document.getElementById('product-catid').value) }
  };
  const url = id ? `${API}/products/${id}` : `${API}/products`;
  const method = id ? 'PUT' : 'POST';
  await fetch(url, { method, headers: { 'Content-Type': 'application/json' }, body: JSON.stringify(body) });
  closeModal();
  loadProducts();
}

async function deleteProduct(id) {
  if (confirm('¿Eliminar este producto?')) {
    await fetch(`${API}/products/${id}`, { method: 'DELETE' });
    loadProducts();
  }
}

async function editProduct(id) {
  const res = await fetch(`${API}/products/${id}`);
  const p = await res.json();
  document.getElementById('product-id').value = p.id;
  document.getElementById('product-name').value = p.name;
  document.getElementById('product-desc').value = p.description;
  document.getElementById('product-price').value = p.price;
  document.getElementById('product-stock').value = p.stock;
  document.getElementById('product-minstock').value = p.minStock;
  document.getElementById('product-catid').value = p.category?.id || '';
  document.getElementById('modal-title').textContent = 'Editar Producto';
  openModal();
}

function openModal() { document.getElementById('modal').classList.remove('hidden'); }
function closeModal() {
  document.getElementById('modal').classList.add('hidden');
  document.getElementById('product-id').value = '';
}

loadProducts();