import { generatePDF } from 'react-native-html-to-pdf';
import Share from 'react-native-share';
import { formatPrice, formatDateTime } from '../database/serviceDao';

function escapeHtml(str) {
  return String(str ?? '')
    .replace(/&/g, '&amp;')
    .replace(/</g, '&lt;')
    .replace(/>/g, '&gt;')
    .replace(/"/g, '&quot;');
}

function buildHtml({ user, motorcycle, schedules }) {
  const total = schedules.reduce((sum, s) => sum + (s.price ?? 0), 0);
  const rows = schedules
    .map(
      s => `
      <tr>
        <td>${escapeHtml(s.serviceName)}</td>
        <td>${escapeHtml(formatDateTime(s.timestamp))}</td>
        <td>${escapeHtml(s.duration)} min</td>
        <td style="text-align:right">${escapeHtml(formatPrice(s.price ?? 0))}</td>
      </tr>`
    )
    .join('');

  return `
    <html>
      <body style="font-family: sans-serif; padding: 24px; color: #222;">
        <h1 style="margin-bottom: 4px;">JB Motos</h1>
        <h3 style="margin-top: 0; color: #666;">Comprovante de agendamento</h3>
        <hr/>
        <p><b>Cliente:</b> ${escapeHtml(user?.name)}</p>
        <p><b>Moto:</b> ${escapeHtml(motorcycle.name)} (${escapeHtml(motorcycle.brand)})</p>
        <table style="width:100%; border-collapse: collapse; margin-top: 16px;" border="1" cellpadding="8">
          <tr style="background:#eee;">
            <th align="left">Serviço</th>
            <th align="left">Data e hora</th>
            <th align="left">Duração</th>
            <th align="right">Preço</th>
          </tr>
          ${rows}
        </table>
        <h3 style="text-align:right; margin-top: 16px;">Total: ${escapeHtml(formatPrice(total))}</h3>
      </body>
    </html>`;
}

/**
 * Gera o PDF dos agendamentos da moto e abre a tela de compartilhar.
 * schedules: [{ serviceName, timestamp, duration, price }]
 */
export async function generateAndShareSchedulePdf({ user, motorcycle, schedules }) {
  const fileName = `agendamento_${motorcycle.id}_${Date.now()}`;
  const { filePath } = await generatePDF({
    html: buildHtml({ user, motorcycle, schedules }),
    fileName,
  });

  try {
    await Share.open({
      url: filePath.startsWith('file://') ? filePath : `file://${filePath}`,
      type: 'application/pdf',
      title: 'Compartilhar agendamento',
      failOnCancel: false,
    });
  } catch (e) {
    console.error('Erro ao compartilhar PDF:', e);
    throw e;
  }
}
