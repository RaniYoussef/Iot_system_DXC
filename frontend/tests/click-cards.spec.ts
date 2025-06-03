import { test, expect } from '@playwright/test';

test('TC003 - Click Street Light card and navigate to light-dashboard', async ({ page }) => {
  await page.goto('http://localhost:4200/dashboard');

  const streetLightCard = page.locator('.dashboard-cards .card').nth(1);
  await streetLightCard.click();

  await expect(page).toHaveURL(/.*light-dashboard/);
});

test('TC004 - Air Pollution card is visible and labeled correctly', async ({ page }) => {
  await page.goto('http://localhost:4200/dashboard');

  const card = page.locator('.dashboard-cards .card').nth(2);
  await expect(card.locator('h3')).toHaveText(/Air Pollution Monitoring/i);
});


