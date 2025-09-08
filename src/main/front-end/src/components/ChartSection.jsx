import { Chart } from "chart.js/auto";
import { useEffect, useRef } from "react";

const ChartSection = ({ title, type }) => {

    const canvasRef = useRef();
    const chartRef = useRef();

    useEffect(() => {
        fetch('/react/admin/logs')
            .then(res => res.json())
            .then(data => {
                // console.log(data)
                if (type == 'seven') sevenChart(data)
                else allChart(data);
            })
            .catch(err => console.log(err))

        // 컴포넌트 언마운트 시 차트 제거
        return () => {
            if (chartRef.current) {
                chartRef.current.destroy();
            }
        }
    }, [type]);

    const getDateFormat = date => {
        const year = (date.getFullYear() + '').slice(-2);
        const month = ('0' + (date.getMonth() + 1)).slice(-2);
        const day = ('0' + date.getDate()).slice(-2);
        return year + '-' + month + '-' + day;
    }

    const getThatDays = (today, n) => {
        return new Date(new Date().setDate(today.getDate() - n))
    }

    const sevenChart = data => {
        const ctx = canvasRef.current;
        const today = new Date();
        if (chartRef.current) {
            chartRef.current.destroy();
        }

        const dates= [];
        const counts = [];
        for(let i = 6; i>= 0; i--) {
            const key= getDateFormat(getThatDays(today,i));
            dates.push(key);
            counts.push(data[key] || 0);
        }


        chartRef.current = new Chart(ctx, {
            type: 'line',
            data: {
                labels: [
                    getDateFormat(getThatDays(today, 6)),
                    getDateFormat(getThatDays(today, 5)),
                    getDateFormat(getThatDays(today, 4)),
                    getDateFormat(getThatDays(today, 3)),
                    getDateFormat(getThatDays(today, 2)),
                    getDateFormat(getThatDays(today, 1)),
                    getDateFormat(today)
                ],
                datasets: [{
                    data: [
                        data[getDateFormat(getThatDays(today, 6))] == undefined ? 0 : data[getDateFormat(getThatDays(today, 6))],
                        data[getDateFormat(getThatDays(today, 5))] == undefined ? 0 : data[getDateFormat(getThatDays(today, 5))],
                        data[getDateFormat(getThatDays(today, 4))] == undefined ? 0 : data[getDateFormat(getThatDays(today, 4))],
                        data[getDateFormat(getThatDays(today, 3))] == undefined ? 0 : data[getDateFormat(getThatDays(today, 3))],
                        data[getDateFormat(getThatDays(today, 2))] == undefined ? 0 : data[getDateFormat(getThatDays(today, 2))],
                        data[getDateFormat(getThatDays(today, 1))] == undefined ? 0 : data[getDateFormat(getThatDays(today, 1))],
                        data[getDateFormat(today)] == undefined ? 0 : data[getDateFormat(today)]
                    ],
                    lineTension: 0,
                    backgroundColor: 'transparent',
                    borderColor: '#007bff',
                    borderWidth: 4,
                    pointBackgroundColor: '#007bff'
                }]
            },
            options: {
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        boxPadding: 3
                    }
                }
            }
        });

    }

    const allChart = data => {
        const ctx = canvasRef.current; //현재의 캔버스
        const today = new Date();

        if (chartRef.current) { //차트
            chartRef.current.destroy();
        }

        const dates = []; //날짜
        const counts = []; //숫자
        for(const key in data) {
            dates.push(key);
            counts.push(data[key]);
        }

        chartRef.current = new Chart(ctx, {
            type: 'line',
            data: {
                labels: dates,
                datasets: [{
                    data: counts,
                    lineTension: 0,
                    backgroundColor: 'transparent',
                    borderColor: '#007bff',
                    borderWidth: 4,
                    pointBackgroundColor: '#007bff'
                }]
            },
            options: {
                plugins: {
                    legend: {
                        display: false
                    },
                    tooltip: {
                        boxPadding: 3
                    }
                }
            }
        });
    }

    return (
        <div>
            <h4>{title}</h4>
            <canvas ref={canvasRef} className="my-4 w-100" width="900" height="250"></canvas>
        </div>
    )
}

export default ChartSection;