import { getDB } from './db';
import { appColors } from '../theme';

// ─── Services ─────────────────────────────────────────────────────────────────

export async function getAllServices() {
  const db = await getDB();
  const [result] = await db.executeSql('SELECT * FROM services;');
  return rowsToArray(result);
}

export async function getServiceById(id) {
  const db = await getDB();
  const [result] = await db.executeSql('SELECT * FROM services WHERE id = ?;', [id]);
  return result.rows.length > 0 ? result.rows.item(0) : null;
}

export async function insertService({ name, price, duration, description = '' }) {
  const db = await getDB();
  await db.executeSql(
    'INSERT INTO services (name, price, duration, description) VALUES (?, ?, ?, ?);',
    [name, price, duration, description]
  );
}

export async function updateService({ id, name, price, duration, description = '' }) {
  const db = await getDB();
  await db.executeSql(
    'UPDATE services SET name = ?, price = ?, duration = ?, description = ? WHERE id = ?;',
    [name, price, duration, description, id]
  );
}

export async function deleteService(id) {
  const db = await getDB();
  // Deleta agendamentos do serviço antes de deletar o serviço
  await db.executeSql('DELETE FROM schedules WHERE serviceId = ?;', [id]);
  await db.executeSql('DELETE FROM services WHERE id = ?;', [id]);
}

// ─── Schedules ────────────────────────────────────────────────────────────────

// Equivalente a serviceScheduledDao.getAllOrdered()
export async function getAllSchedulesOrdered() {
  const db = await getDB();
  const [result] = await db.executeSql(
    'SELECT * FROM schedules ORDER BY timestamp ASC;'
  );
  return rowsToArray(result);
}

// Equivalente a serviceScheduledDao.getByMotorcycleIdOrdered()
export async function getSchedulesByMotorcycleId(motorcycleId) {
  const db = await getDB();
  const [result] = await db.executeSql(
    'SELECT * FROM schedules WHERE motorcycleId = ? ORDER BY timestamp ASC;',
    [motorcycleId]
  );
  return rowsToArray(result);
}

export async function insertSchedule({ serviceId, motorcycleId, dateTime }) {
  // dateTime é um objeto Date — salvo como timestamp em milissegundos (igual ao Android)
  const db = await getDB();
  await db.executeSql(
    'INSERT INTO schedules (serviceId, motorcycleId, timestamp) VALUES (?, ?, ?);',
    [serviceId, motorcycleId, dateTime.getTime()]
  );
}

export async function deleteSchedule(id) {
  const db = await getDB();
  await db.executeSql('DELETE FROM schedules WHERE id = ?;', [id]);
}

/**
 * Retorna os horários ocupados por data.
 * Equivalente a ServiceScheduled.getUnavailableDateTimes()
 *
 * Retorna: { 'yyyy-mm-dd': [{ start: Date, end: Date }] }
 */
export async function getUnavailableDateTimes() {
  const db = await getDB();
  const [schedulesResult] = await db.executeSql(
    'SELECT s.*, sv.duration FROM schedules s JOIN services sv ON s.serviceId = sv.id;'
  );
  const schedules = rowsToArray(schedulesResult);

  const map = {};
  for (const row of schedules) {
    const start = new Date(row.timestamp);
    const end = new Date(row.timestamp + row.duration * 60 * 1000);
    const dateKey = start.toISOString().split('T')[0]; // 'yyyy-mm-dd'

    if (!map[dateKey]) map[dateKey] = [];
    map[dateKey].push({ start, end });
  }
  return map;
}

// ─── Helpers ──────────────────────────────────────────────────────────────────

function rowsToArray(result) {
  const arr = [];
  for (let i = 0; i < result.rows.length; i++) {
    arr.push(result.rows.item(i));
  }
  return arr;
}

/** Formata preço em centavos para "R$ X,XX" */
export function formatPrice(cents) {
  return `R$ ${Math.floor(cents / 100)},${String(cents % 100).padStart(2, '0')}`;
}

/** Formata timestamp (ms) para "dd/mm/aaaa HH:MM" */
export function formatDateTime(timestamp) {
  const d = new Date(timestamp);
  const pad = n => String(n).padStart(2, '0');
  return `${pad(d.getDate())}/${pad(d.getMonth() + 1)}/${d.getFullYear()} ${pad(d.getHours())}:${pad(d.getMinutes())}`;
}