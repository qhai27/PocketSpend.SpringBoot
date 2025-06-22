// Currency Converter Module
class CurrencyConverter {
    constructor() {
        this.exchangeRates = {};
        this.lastFetchTime = 0;
        this.CACHE_DURATION = 5 * 60 * 1000; // 5 minutes

        this.initializeElements();
        this.bindEvents();
        this.fetchExchangeRates();
    }

    initializeElements() {
        this.fromAmount = document.getElementById('from-amount');
        this.toAmount = document.getElementById('to-amount');
        this.fromCurrency = document.getElementById('from-currency');
        this.toCurrency = document.getElementById('to-currency');
        this.convertBtn = document.getElementById('convert-btn');
        this.exchangeRateInfo = document.getElementById('exchange-rate-info');
        this.exchangeRate = document.getElementById('exchange-rate');
        this.fromCurrencyLabel = document.getElementById('from-currency-label');
        this.toCurrencyLabel = document.getElementById('to-currency-label');
        this.lastUpdated = document.getElementById('last-updated');
    }

    bindEvents() {
        // Convert button
        if (this.convertBtn) {
            this.convertBtn.addEventListener('click', () => this.convertCurrency());
        }

        // Auto-convert when amount changes
        if (this.fromAmount) {
            this.fromAmount.addEventListener('input', () => {
                if (this.fromAmount.value && parseFloat(this.fromAmount.value) > 0) {
                    this.convertCurrency();
                } else {
                    this.toAmount.value = '';
                    this.exchangeRateInfo.classList.add('hidden');
                }
            });
        }

        // Auto-convert when currency changes
        if (this.fromCurrency) {
            this.fromCurrency.addEventListener('change', () => {
                if (this.fromAmount.value && parseFloat(this.fromAmount.value) > 0) {
                    this.convertCurrency();
                }
            });
        }

        if (this.toCurrency) {
            this.toCurrency.addEventListener('change', () => {
                if (this.fromAmount.value && parseFloat(this.fromAmount.value) > 0) {
                    this.convertCurrency();
                }
            });
        }

        // Popular conversion buttons
        document.addEventListener('click', (e) => {
            if (e.target.closest('.popular-conversion')) {
                const button = e.target.closest('.popular-conversion');
                const from = button.dataset.from;
                const to = button.dataset.to;

                this.fromCurrency.value = from;
                this.toCurrency.value = to;

                if (this.fromAmount.value && parseFloat(this.fromAmount.value) > 0) {
                    this.convertCurrency();
                }
            }
        });
    }

    async fetchExchangeRates() {
        const now = Date.now();
        if (now - this.lastFetchTime < this.CACHE_DURATION && Object.keys(this.exchangeRates).length > 0) {
            return this.exchangeRates;
        }

        try {
            // Using a free currency API (ExchangeRate-API)
            const response = await fetch('https://api.exchangerate-api.com/v4/latest/MYR');
            if (!response.ok) {
                throw new Error('Failed to fetch exchange rates');
            }

            const data = await response.json();
            this.exchangeRates = data.rates;
            this.lastFetchTime = now;

            // Update popular conversion rates
            this.updatePopularRates();

            return this.exchangeRates;
        } catch (error) {
            console.error('Error fetching exchange rates:', error);
            this.showToast('Failed to fetch exchange rates. Using cached rates.', 'error');
            return this.exchangeRates;
        }
    }

    updatePopularRates() {
        if (this.exchangeRates.USD) {
            const myrUsdRate = document.querySelector('.myr-usd-rate');
            const usdMyrRate = document.querySelector('.usd-myr-rate');
            if (myrUsdRate) myrUsdRate.textContent = this.exchangeRates.USD.toFixed(4);
            if (usdMyrRate) usdMyrRate.textContent = (1 / this.exchangeRates.USD).toFixed(4);
        }
        if (this.exchangeRates.SGD) {
            const myrSgdRate = document.querySelector('.myr-sgd-rate');
            if (myrSgdRate) myrSgdRate.textContent = this.exchangeRates.SGD.toFixed(4);
        }
    }

    async convertCurrency() {
        const amount = parseFloat(this.fromAmount.value);
        const from = this.fromCurrency.value;
        const to = this.toCurrency.value;

        if (!amount || amount <= 0) {
            this.showToast('Please enter a valid amount', 'error');
            return;
        }

        if (from === to) {
            this.toAmount.value = amount.toFixed(2);
            this.showExchangeRateInfo(1, from, to);
            return;
        }

        this.convertBtn.disabled = true;
        this.convertBtn.innerHTML = '🔄 Converting...';

        try {
            const rates = await this.fetchExchangeRates();

            let rate;
            if (from === 'MYR') {
                rate = rates[to] || 1;
            } else if (to === 'MYR') {
                rate = 1 / (rates[from] || 1);
            } else {
                // Convert through MYR as base
                const fromRate = rates[from] || 1;
                const toRate = rates[to] || 1;
                rate = toRate / fromRate;
            }

            const convertedAmount = amount * rate;
            this.toAmount.value = convertedAmount.toFixed(2);

            this.showExchangeRateInfo(rate, from, to);
            this.showToast('Currency converted successfully! 💱', 'success');

        } catch (error) {
            console.error('Conversion error:', error);
            this.showToast('Failed to convert currency', 'error');
        } finally {
            this.convertBtn.disabled = false;
            this.convertBtn.innerHTML = '🔄 Convert Currency';
        }
    }

    showExchangeRateInfo(rate, from, to) {
        this.exchangeRate.textContent = rate.toFixed(4);
        this.fromCurrencyLabel.textContent = from;
        this.toCurrencyLabel.textContent = to;
        this.lastUpdated.textContent = new Date().toLocaleTimeString();
        this.exchangeRateInfo.classList.remove('hidden');
    }

    showToast(message, type = 'success') {
        // Use the existing showToast function from index.html
        if (typeof showToast === 'function') {
            showToast(message, type);
        } else {
            // Fallback toast implementation
            console.log(`${type.toUpperCase()}: ${message}`);
        }
    }
}

// Initialize currency converter when DOM is loaded
document.addEventListener('DOMContentLoaded', function () {
    // Only initialize if we're on a page with currency converter elements
    if (document.getElementById('currency-converter')) {
        new CurrencyConverter();
    }
});

// Export for potential use in other modules
if (typeof module !== 'undefined' && module.exports) {
    module.exports = CurrencyConverter;
} 